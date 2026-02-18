package com.liquordb.service;

import com.liquordb.dto.user.*;
import com.liquordb.entity.User;
import com.liquordb.enums.Role;
import com.liquordb.enums.UserStatus;
import com.liquordb.exception.user.*;
import com.liquordb.mapper.UserMapper;
import com.liquordb.repository.UserRepository;
import com.liquordb.security.JwtInformation;
import com.liquordb.security.JwtRegistry;
import com.liquordb.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final FileService fileService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtRegistry jwtRegistry;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String RESET_LINK_PREFIX = "https://liquordb.com/password/reset?token=";  // 실제로 작동하지는 않는 링크임.
    private static final String RESET_MAIL_SUBJECT = "[LiquorDB] 비밀번호 재설정 안내드립니다.";
    private static final long RESET_TOKEN_EXPIRATION_MINUTES = 5;

    // 회원가입
    @Transactional
    public UserResponseDto signUp(SignUpRequest request, MultipartFile profileImage, Role role) {

        String email = request.email();
        String username = request.username();

        User existingUser = userRepository.findByEmail(email)
                .orElse(null);
        if (existingUser != null) {
            if (existingUser.getStatus().equals(UserStatus.BANNED)) {
                throw new BannedUserException();
            } else if (existingUser.getStatus().equals(UserStatus.WITHDRAWN)) {
                throw new WithdrawnUserException();
            } else {
                throw new DuplicateEmailException(email);
            }
        }
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException(username);
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = UserMapper.toEntity(request, encodedPassword, role);
        if (profileImage != null) {
            user.setProfileImage(fileService.upload(profileImage));
        }
        userRepository.save(user);

        return UserMapper.toDto(user);
    }

    // 로그인
    @Transactional
    public JwtInformation login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(LoginFailedException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new LoginFailedException();
        }
        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new WithdrawnUserException();
        } else if (user.getStatus() == UserStatus.BANNED) {
            throw new BannedUserException();
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getUsername(), user.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUsername(), user.getRole().name());

        jwtRegistry.registerRefreshToken(user.getId(), refreshToken);

        return new JwtInformation(UserMapper.toDto(user), accessToken, refreshToken);
    }

    // 토큰 재발급 (엑세스 토큰 재발급 + 리프레시 토큰도 바꿈)
    public JwtInformation refresh(String refreshToken) {

        // 서명 유효성, DB 존재 여부 확인
        if (refreshToken == null
                || !jwtTokenProvider.validateRefreshToken(refreshToken)
                || !jwtRegistry.isRefreshTokenActive(refreshToken)
        ) {
            throw new InvalidTokenException(); // TODO Access, Refresh 분리?
        }

        // 정보 조회
        String username = jwtTokenProvider.getClaims(refreshToken).getSubject();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        // 새 토큰 생성 - Access, Refresh 모두 새로 발급
        String newAccess = jwtTokenProvider.createAccessToken(username, user.getRole().name());
        String newRefresh = jwtTokenProvider.createRefreshToken(username, user.getRole().name());

        JwtInformation newInfo = new JwtInformation(
                UserMapper.toDto(user),
                newAccess,
                newRefresh
        );

        jwtRegistry.rotateRefreshToken(refreshToken, newRefresh, user.getId());

        return newInfo;
    }

    // 비밀번호 재설정 링크 전송
    @Transactional
    public void sendPasswordResetLink(PasswordFindRequest request) {
        String email = request.email();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (user.getStatus().equals(UserStatus.BANNED)) {
            throw new BannedUserException();
        }
        // 자발적으로 탈퇴한 유저의 비밀번호 재설정은 허용됨. 계정 복구를 위해선 로그인이 필요하기 때문.

        String resetToken = UUID.randomUUID().toString();

        stringRedisTemplate.opsForValue().set(resetToken, email, Duration.ofMinutes(RESET_TOKEN_EXPIRATION_MINUTES));

        String resetLink = RESET_LINK_PREFIX + resetToken;

        final String resetMailText
                = "안녕하세요. LiquorDB입니다.\n\n" +
                "비밀번호 재설정을 위해 아래 링크를 클릭해 주세요.\n" +
                resetLink + "\n\n" +
                "이 링크는 5분 동안만 유효합니다.";

        mailService.sendMail(user.getEmail(), RESET_MAIL_SUBJECT, resetMailText); // 비동기
    }

    // 비밀번호 재설정
    @Transactional
    public void resetPasswordByToken(PasswordResetRequest request) {

        String token = request.token();
        String email = stringRedisTemplate.opsForValue().get(token);
        if (email == null) {
            throw new InvalidTokenException();
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        user.updatePassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        stringRedisTemplate.delete(token);
    }

    // 계정 복구
    @Transactional
    public UserResponseDto restore(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(LoginFailedException::new);
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new LoginFailedException();
        }
        user.restore();
        userRepository.save(user);
        return UserMapper.toDto(user);
    }
}

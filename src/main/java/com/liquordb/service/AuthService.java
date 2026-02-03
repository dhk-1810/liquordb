package com.liquordb.service;

import com.liquordb.dto.user.*;
import com.liquordb.entity.User;
import com.liquordb.enums.Role;
import com.liquordb.enums.UserStatus;
import com.liquordb.exception.user.*;
import com.liquordb.mapper.UserMapper;
import com.liquordb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final FileService fileService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    private static final String RESET_LINK_PREFIX = "https://liquordb.com/password/reset?token=";  // 실제로 작동하지는 않는 링크임.
    private static final String RESET_MAIL_SUBJECT = "[LiquorDB] 비밀번호 재설정 안내드립니다.";

    // 회원가입
    @Transactional
    public UserResponseDto signUp(SignUpRequestDto request, MultipartFile profileImage, Role role) {

        String email = request.email();
        User existingUser = userRepository.findByEmail(email)
                .orElse(null);

        if (existingUser != null) {
            if (existingUser.getStatus().isAvailable()) {
                throw new DuplicateEmailException(email);
            } else if (existingUser.getStatus().equals(UserStatus.BANNED)) {
                throw new BannedUserException();
            } else if (existingUser.getStatus().equals(UserStatus.WITHDRAWN)) {
                throw new WithdrawnUserException();
            }
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
    // TODO 사라질 운명. 시큐리티에서 대체.
    @Transactional
    public UserResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(LoginFailedException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new LoginFailedException();
        }
        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new WithdrawnUserException();
        }

        return UserMapper.toDto(user);
    }

    // 비밀번호 재설정 링크 전송
    @Transactional
    public void sendPasswordResetLink(PasswordFindRequestDto request) {
        String email = request.email();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (user.getStatus().equals(UserStatus.BANNED)) {
            throw new BannedUserException();
        }
        // 자발적으로 탈퇴한 유저의 비밀번호 재설정은 허용됨. 계정 복구를 위해선 로그인이 필요하기 때문.

        String resetToken = UUID.randomUUID().toString();

        // TODO Redis 설정
        redisTemplate.opsForValue().set(resetToken, email, Duration.ofMinutes(10));

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
        String email = redisTemplate.opsForValue().get(token);
        if (email == null) {
            throw new InvalidTokenException();
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        user.updatePassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        redisTemplate.delete(token);
    }

    // 계정 복구
    // TODO 시큐리티로 대체
    @Transactional
    public UserResponseDto restore(LoginRequestDto request) {
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

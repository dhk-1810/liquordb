package com.liquordb.service;

import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.dto.user.*;
import com.liquordb.entity.*;
import com.liquordb.enums.Role;
import com.liquordb.enums.UserStatus;
import com.liquordb.exception.user.*;
import com.liquordb.mapper.CommentMapper;
import com.liquordb.mapper.LiquorMapper;
import com.liquordb.mapper.ReviewMapper;
import com.liquordb.mapper.UserMapper;
import com.liquordb.repository.*;
import com.liquordb.dto.liquor.LiquorSummaryDto;

import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.dto.review.ReviewSummaryDto;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final LiquorLikeRepository liquorLikeRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final CommentLikeRepository commentLikeRepository;

    private final UserTagService userTagService;
    private final FileService fileService;
    private final LiquorMapper liquorMapper;
    private final CommentMapper commentMapper;

    private final JavaMailSender mailSender;
    private final OAuth2UserService oAuth2UserService;

    // 회원가입
    @Transactional
    public UserResponseDto register(UserRegisterRequestDto request, MultipartFile profileImage, Role role) {

        String email = request.email();
        User existingUser = userRepository.findByEmail(email)
                .orElse(null);

        if (existingUser != null) {
            if (existingUser.getStatus().isActiveUser()) {
                throw new EmailAlreadyExistsException(email);
            }
            if (existingUser.getStatus().equals(UserStatus.BANNED)) {
                throw new BannedUserException(email);
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
    @Transactional
    public UserResponseDto login(UserLoginRequestDto dto) {
        String email = dto.email();
        User user = userRepository.findByEmail(email)
                .orElseThrow(LoginFailedException::new);

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new LoginFailedException();
        }

        if (user.getStatus().equals(UserStatus.BANNED)) {
            throw new BannedUserException(email);
        }

        return UserMapper.toDto(user);
    }

    // 회원 탈퇴 (soft delete)
    @Transactional
    public void withdraw(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.withdraw();
        user.update(user.getEmail() + LocalDate.now(), null);
        userRepository.save(user);
    }

    // TODO 계정 복구

    // 마이페이지
    @Transactional
    // TODO @PreAuthorize()
    public UserMyPageResponseDto getMyPageInfo(UUID userId, boolean showAllTags) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 리뷰 작성 목록
        List<ReviewSummaryDto> createdReviews
                = reviewRepository.findAllByUser_IdAndStatus(userId, Review.ReviewStatus.ACTIVE)
                .stream()
                .map(ReviewMapper::toSummaryDto)
                .toList();

        // 댓글 작성 목록
        List<CommentResponseDto> createdComments = commentRepository.findAllByUser_Id(userId).stream()
                .map(commentMapper::toDto)
                .toList();

        // 좋아요한 주류, 리뷰, 댓글 목록
        List<LiquorSummaryDto> likedLiquors = liquorLikeRepository.findByUser_IdAndLiquorIsDeletedFalse(userId).stream()
                .map(liquorLike -> liquorMapper.toSummaryDto(liquorLike.getLiquor(), user))
                .toList();
        List<ReviewResponseDto> likedReviews = reviewLikeRepository.findByUser_IdAndReviewIsHiddenFalse(userId).stream()
                .map(reviewLike -> ReviewMapper.toDto(reviewLike.getReview()))
                .toList();
        List<CommentResponseDto> likedComments = commentLikeRepository.findByUser_IdAndCommentIsHiddenFalse(userId).stream()
                .map(commentLike -> commentMapper.toDto(commentLike.getComment()))
                .toList();

        long reviewCount = createdReviews.size();
        long commentCount = createdComments.size();
        long likedLiquorCount = likedLiquors.size();
        long likedReviewCount = likedReviews.size();
        long likedCommentCount = likedComments.size();


        // 등록한(=선호하는) 태그 목록
        List<TagResponseDto> preferredTags = userTagService.findByUserId(userId, showAllTags);

        return UserMyPageResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .reviewCount(reviewCount)
                .commentCount(commentCount)
                .likedLiquorCount(likedLiquorCount)
                .likedReviewCount(likedReviewCount)
                .likedCommentCount(likedCommentCount)
                .likedLiquors(likedLiquors)
                .likedReviews(likedReviews)
                .likedComments(likedComments)
                .reviewList(createdReviews)
                .commentList(createdComments)
                .preferredTags(preferredTags)
                .build();
    }

    // 회원정보수정 (닉네임, 프사)
    @Transactional
    // TODO @PreAuthorize()
    public void update(UUID userId, UserUpdateRequestDto request, MultipartFile newProfileImage) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String email = request.email();
        if (email != null && !userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        String username = request.username();
        if (username != null && !userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(username);
        }

        if (request.deleteProfileImage()) {
            fileService.delete(user.getProfileImage().getId());
            user.setProfileImage(null);
        }

        if (newProfileImage != null && !newProfileImage.isEmpty()) {
            File profileImage = fileService.upload(newProfileImage);
            user.setProfileImage(profileImage);
        }

        userRepository.save(user);
    }

    // 비밀번호 재설정 (마이페이지)
    @Transactional
    public void updatePassword(UUID userId, UserUpdatePasswordDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        user.updatePassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }

    // TODO 나중에 구현
    /*
    // 비밀번호 찾기 - 재설정 링크 전송
    @Transactional
    public void sendPasswordResetLink(UserFindPasswordRequestDto dto) {
        String email = dto.getEmail();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null || user.getStatus().equals(UserStatus.BANNED)) {
            return;
        }

        String resetToken = UUID.randomUUID().toString();

        // TODO Redis에 저장
        user.updatePasswordResetToken(resetToken, LocalDateTime.now().plusMinutes(30));

        // 4. 재설정 페이지 URL 생성 및 전송
        String resetLink = "https://your-service.com/password/reset?token=" + resetToken;
        mail.sendResetLink(user.getEmail(), resetLink);
    }

    // 비밀번호 재설정 (찾기 후)
    @Transactional
    public void resetPasswordByToken(String token, String newPassword) {
        // 1. 토큰으로 유저 찾기 (유효 시간 검증 포함)
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new InvalidTokenException("유효하지 않거나 만료된 토큰입니다."));

        // 2. 토큰 만료 시간 체크 (엔터티 내부 로직 권장)
        if (user.isResetTokenExpired()) {
            throw new InvalidTokenException("만료된 토큰입니다.");
        }

        // 3. 비밀번호 변경 및 토큰 초기화 (재사용 방지)
        user.setPassword(passwordEncoder.encode(newPassword));
        user.clearResetToken(); // 다시는 이 토큰으로 접근 못하게 비움

        userRepository.save(user);
    }


    @Value("${spring.mail.username}")
    private String fromEmail;

//    // 생성된 비밀번호를 이메일로 발송
//    public void sendTempPassword(String toEmail, String tempPassword) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(toEmail);
//        message.setFrom(fromEmail);
//        message.setSubject("임시 비밀번호 안내");
//        message.setText("임시 비밀번호는 다음과 같습니다: " + tempPassword);
//
//        mailSender.send(message);
//    }
*/
    /**
     * 관리자용 메서드
     */

    // 유저 조회 - 전체 또는 검색
    public List<UserResponseDto> getUsers(String keyword, UserStatus status) {
        if ((keyword == null || keyword.isBlank()) && status == null) {
            return userRepository.findAll().stream()
                    .map(UserMapper::toDto)
                    .toList();
        }

        // 조건 검색이 가능하도록 사용자 정의 메서드 또는 Specification 사용
        return userRepository.search(keyword, status).stream()
                .map(UserMapper::toDto)
                .toList();
    }

    // 유저 이용제한
    // TODO ReportService로 옮기기. 신고 승인시 자동 처리.
    public UserResponseDto restrictUser(UUID userId, String period) {

        User user = userRepository.findActiveOrSuspendedUser(userId, List.of(UserStatus.ACTIVE, UserStatus.SUSPENDED))
                .orElseThrow(() -> new UserNotFoundException(userId));

        long days = switch (period) {
            case "WARNING" -> 0;
            case "1D" -> 1;
            case "3D" -> 3;
            case "7D" -> 7;
            case "1M" -> 30;
            case "3M" -> 90;
            default -> throw new IllegalArgumentException("유효하지 않은 기간입니다.");
        };

        if (days > 0) {
            user.suspend(LocalDateTime.now().plusDays(days));
        }

        return UserMapper.toDto(userRepository.save(user));
    }
}


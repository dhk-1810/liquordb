package com.liquordb.service;

import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.dto.user.*;
import com.liquordb.entity.*;
import com.liquordb.enums.UserStatus;
import com.liquordb.exception.NotFoundException;
import com.liquordb.mapper.CommentMapper;
import com.liquordb.mapper.UserMapper;
import com.liquordb.repository.CommentLikeRepository;
import com.liquordb.repository.LiquorLikeRepository;
import com.liquordb.repository.ReviewLikeRepository;
import com.liquordb.dto.liquor.LiquorSummaryDto;

import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.dto.review.ReviewSummaryDto;
import com.liquordb.repository.UserRepository;
import com.liquordb.repository.ReviewRepository;
import com.liquordb.repository.CommentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 일반 유저 서비스 클래스입니다.
 */
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
    private final LiquorLikeService liquorLikeService;
    private final FileService fileService;
    private final JavaMailSender mailSender;
    private final ReviewLikeService reviewLikeService;
    private final CommentLikeService commentLikeService;

    // 회원가입
    @Transactional
    public UserResponseDto register(UserRegisterRequestDto dto, MultipartFile profileImage, User.Role role) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다."); // 강제 탈퇴(BANNED) 된 경우에도 여기서 예외.
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        User user = User.builder()
                .email(dto.getEmail())
                .password(encodedPassword)
                .nickname(dto.getNickname())
                .role(role)
                .build();

        if (profileImage != null) {
            user.setProfileImage(fileService.upload(profileImage));
        }

        return UserMapper.toDto(userRepository.save(user));
    }

    // 로그인
    @Transactional
    public UserLoginResponseDto login(UserLoginRequestDto dto) {
        String password = dto.getPassword();

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new NotFoundException("잘못된 이메일입니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if (user.getStatus().equals(UserStatus.BANNED)) {
            throw new IllegalArgumentException("신고 누적으로 강제 탈퇴된 계정입니다. 서비스 이용이 불가능합니다.");
        }

//        // TODO 계정 삭제 후 복귀한 유저 처리
//        if (user.getStatus().equals(UserStatus.WITHDRAWN)) {
//            user.setStatus(UserStatus.ACTIVE);
//        }

        return UserLoginResponseDto.from(user);
    }

    // 비밀번호 찾기 (임시 비밀번호 발급)
    @Transactional
    public void findPasswordAndSend(UserFindPasswordRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));

        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        // 이메일 전송
        sendTempPassword(user.getEmail(), tempPassword);
    }

    // 회원 탈퇴 (soft delete)
    @Transactional
    public void delete(UUID userId) {
        User user = userRepository.findByIdAndStatusNot(userId, UserStatus.WITHDRAWN)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));

        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new IllegalStateException("이미 탈퇴한 사용자입니다.");
        }

        user.setStatus(UserStatus.WITHDRAWN);
        user.setWithdrawnAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // 마이페이지
    @Transactional
    public UserMyPageResponseDto getMyPageInfo(UUID userId, boolean showAllTags) {

        User user = userRepository.findByIdAndStatusNot(userId, UserStatus.WITHDRAWN)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));

        if (user.getStatus().equals(UserStatus.WITHDRAWN)
                || user.getStatus().equals(UserStatus.BANNED)) {
            throw new IllegalArgumentException("계정이 비활성화된 유저입니다.");
        }

        // N+1 문제 방지 위해 리포지토리 메서드 사용.
        long reviewCount = reviewRepository.countByUserAndStatus(user, Review.ReviewStatus.ACTIVE);
        long commentCount = commentRepository.countByUserAndStatus(user, Comment.CommentStatus.ACTIVE);
        long likedLiquorCount = liquorLikeRepository.countByUserAndLiquorIsHiddenFalse(user);
        long likedReviewCount = reviewLikeRepository.countByUserAndReviewIsHiddenFalse(user);
        long likedCommentCount = commentLikeRepository.countByUserAndCommentIsHiddenFalse(user);

        // 리뷰 작성 목록
        List<ReviewSummaryDto> createdReviews = user.getReviews().stream()
                .map(ReviewSummaryDto::from)
                .toList();

        // 댓글 작성 목록
        List<CommentResponseDto> createdComments = user.getComments().stream()
                .map(CommentMapper::toDto)
                .toList();

        // 좋아요한 주류, 리뷰, 댓글 목록
        List<LiquorSummaryDto> likedLiquors = liquorLikeService.getLiquorSummaryDtosByUserId(userId);
        List<ReviewResponseDto> likedReviews = reviewLikeService.getReviewSummaryDtosByUserId(userId);
        List<CommentResponseDto> likedComments = commentLikeService.getCommentSummaryDtosByUserId(userId);

        // 등록한(=선호하는) 태그 목록
        List<String> preferredTags = userTagService.findTagNamesByUserId(userId);

        return UserMyPageResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
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
    public void update(UUID userId, UserUpdateRequestDto dto, MultipartFile newProfileImage) {

        User user = userRepository.findByIdAndStatusNot(userId, UserStatus.WITHDRAWN)
                .orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다."));

        if (dto.getEmail() != null) {
            if (!userRepository.existsByEmail(dto.getEmail())) {
                user.setEmail(dto.getEmail());
            }
        }

        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }

        if (dto.isDeleteProfileImage()) {
            fileService.delete(user.getProfileImage().getId());
            user.setProfileImage(null);
        }

        if (newProfileImage != null && !newProfileImage.isEmpty()) {
            File profileImage = fileService.upload(newProfileImage);
            user.setProfileImage(profileImage);
        }

        userRepository.save(user);
    }

    // 비밀번호 재설정
    @Transactional
    public void updatePassword(UUID userId, UserUpdatePasswordDto dto) {
        User user = userRepository.findByIdAndStatusNot(userId, UserStatus.WITHDRAWN)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));

        // 민감한 변경이므로 UserStatus 체크.
        if (user.getStatus().equals(UserStatus.WITHDRAWN) || user.getStatus().equals(UserStatus.BANNED)) {
            throw new IllegalArgumentException("탈퇴 처리된 유저입니다.");
        }

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    @Value("${spring.mail.username}")
    private String fromEmail;

    // 생성된 비밀번호를 이메일로 발송
    public void sendTempPassword(String toEmail, String tempPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom(fromEmail);
        message.setSubject("임시 비밀번호 안내");
        message.setText("임시 비밀번호는 다음과 같습니다: " + tempPassword);

        mailSender.send(message);
    }

    /**
     * 관리자용 메서드
     */

    // 유저 조회 - 전체 또는 검색
    public List<UserResponseDto> searchUsers(String keyword, UserStatus status) {
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
    public UserResponseDto restrictUser(UUID userId, String period) {
        User user = userRepository.findByIdAndStatusNot(userId, UserStatus.WITHDRAWN)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

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
            user.setRestrictedUntil(LocalDateTime.now().plusDays(days));
            user.setStatus(UserStatus.RESTRICTED);
        } else {
            user.setStatus(UserStatus.WARNED);  // 경고 상태 부여
        }

        return UserMapper.toDto(userRepository.save(user));
    }
}


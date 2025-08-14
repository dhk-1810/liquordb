package com.liquordb.user.service;

import com.liquordb.like.entity.LikeTargetType;
import com.liquordb.liquor.dto.LiquorSummaryDto;

import com.liquordb.liquor.repository.LiquorRepository;
import com.liquordb.liquor.repository.LiquorTagRepository;
import com.liquordb.review.dto.CommentResponseDto;
import com.liquordb.review.dto.ReviewSummaryDto;
import com.liquordb.tag.service.TagService;
import com.liquordb.user.dto.*;
import com.liquordb.user.entity.User;
import com.liquordb.user.entity.UserLevel;
import com.liquordb.user.entity.UserStatus;
import com.liquordb.user.repository.UserLevelRepository;
import com.liquordb.user.repository.UserRepository;
import com.liquordb.review.repository.ReviewRepository;
import com.liquordb.review.repository.CommentRepository;
import com.liquordb.like.repository.LikeRepository;

import com.liquordb.user.repository.UserTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 일반 유저 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final UserTagRepository userTagRepository;
    private final UserLevelRepository userLevelRepository;
    private final LiquorTagRepository liquorTagRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final LiquorRepository liquorRepository;

    private final TagService tagService;
    private final UserService userService;
    private final FileUploadService fileUploadService;

    // 회원가입
    public UserResponseDto register(UserRegisterRequestDto dto) {
        if (userRepository.existsByEmailAndIsDeletedFalse(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        User user = User.builder()
                .email(dto.getEmail())
                .password(encodedPassword)
                .nickname(dto.getNickname())
                .role(User.Role.USER)
                .build();

        return UserResponseDto.from(userRepository.save(user));
    }

    // 로그인
    public UserLoginResponseDto login(UserLoginRequestDto dto) {
        String email = dto.getEmail();
        String pw = dto.getPassword();

        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(pw, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return UserLoginResponseDto.from(user);
    }

    // 비밀번호 찾기 (임시 비밀번호 발급)
    public void findPasswordAndSend(UserFindPasswordRequestDto dto) {
        User user = userRepository.findByEmailAndIsDeletedFalse(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 사용자가 없습니다."));

        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        // 이메일 전송
        userService.sendTempPassword(user.getEmail(), tempPassword);
    }

    // 회원 탈퇴 (soft delete)
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new IllegalStateException("이미 탈퇴한 사용자입니다.");
        }

        user.setStatus(UserStatus.WITHDRAWN);
        userRepository.save(user);
    }

    // 마이페이지
    public UserMyPageResponseDto getMyPageInfo(Long userId, boolean showAllTags) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Long reviewCount = reviewRepository.countByUserId(userId);
        Long commentCount = commentRepository.countByUserId(userId);

        Long likedLiquorCount = likeRepository.countByUserIdAndTargetType(userId, LikeTargetType.LIQUOR);
        Long likedReviewCount = likeRepository.countByUserIdAndTargetType(userId, LikeTargetType.REVIEW);
        Long likedCommentCount = likeRepository.countByUserIdAndTargetType(userId, LikeTargetType.COMMENT);

        // 리뷰 작성 목록
        List<ReviewSummaryDto> reviewList = reviewRepository.findByUserId(userId).stream()
                .map(ReviewSummaryDto::from)
                .toList();

        // 댓글 작성 목록
        List<CommentResponseDto> commentList = commentRepository.findByUserId(userId).stream()
                .map(comment -> CommentResponseDto.from(
                        comment,
                        likeRepository.countByTargetIdAndTargetType(comment.getId(), LikeTargetType.COMMENT)
                ))
                .toList();

        // 좋아요한 주류 목록
        List<LiquorSummaryDto> likedLiquors = likeRepository.findByUserIdAndTargetType(userId, LikeTargetType.LIQUOR).stream()
                .map(like -> liquorRepository.findById(like.getTargetId())
                        .map(LiquorSummaryDto::from)
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        // 좋아요한 리뷰 목록
        List<ReviewSummaryDto> likedReviews = likeRepository.findByUserIdAndTargetType(userId, LikeTargetType.REVIEW).stream()
                .map(like -> reviewRepository.findById(like.getTargetId())
                        .map(ReviewSummaryDto::from)
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        // 좋아요한 댓글 목록 (likeCount 포함)
        List<CommentResponseDto> likedComments = likeRepository.findByUserIdAndTargetType(userId, LikeTargetType.COMMENT).stream()
                .map(like -> commentRepository.findById(like.getTargetId())
                        .map(comment -> CommentResponseDto.from(
                                comment,
                                likeRepository.countByTargetIdAndTargetType(comment.getId(), LikeTargetType.COMMENT)
                        ))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        // 등록한(=선호하는) 태그 목록
        List<String> preferredTags = tagService.getPreferredTagsForUser(userId, showAllTags);

        return UserMyPageResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .level(user.getUserLevel().getName())
                .reviewCount(reviewCount)
                .commentCount(commentCount)
                .likedLiquorCount(likedLiquorCount)
                .likedReviewCount(likedReviewCount)
                .likedCommentCount(likedCommentCount)
                .preferredTags(preferredTags)
                .build();
    }

    // 회원정보수정 (프사, 닉네임)
    public void updateUser(Long userId, UserUpdateRequestDto dto) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }

        if (dto.getProfileImage() != null && !dto.getProfileImage().isEmpty()) {
            String imageUrl = fileUploadService.upload(dto.getProfileImage());
            user.setProfileImageUrl(imageUrl);
        }

        userRepository.save(user);
    }

    // 비밀번호 재설정
    public void updatePassword(Long userId, UserUpdatePasswordDto dto) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    // 비밀번호 찾기 (임시 비밀번호 전송)
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendTempPassword(String toEmail, String tempPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom(fromEmail);
        message.setSubject("임시 비밀번호 안내");
        message.setText("임시 비밀번호는 다음과 같습니다: " + tempPassword);

        mailSender.send(message);
    }

    /**
     * 유저가 선택한 태그별로 주류 목록 조회
     * @param userId 유저 아이디
     * @return 유저가 선호하는 태그가 붙은 주류 리스트 (중복 제거)
     */
    @Transactional(readOnly = true)
    public List<LiquorSummaryDto> getLiquorsByUserPreferredTags(Long userId) {
        // 1. 유저가 선택한 태그 아이디 목록 조회
        List<Long> preferredTagIds = userTagRepository.findTagIdsByUserId(userId);

        if (preferredTagIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 해당 태그들을 가진 주류 아이디 목록 조회 (중복 가능)
        List<Long> liquorIds = liquorTagRepository.findLiquorIdsByTagIds(preferredTagIds);

        if (liquorIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 중복 제거 및 주류 요약 DTO 리스트로 변환
        return liquorRepository.findAllById(liquorIds).stream()
                .map(LiquorSummaryDto::from)
                .distinct()
                .toList();
    }

    // 유저 레벨업
    @Transactional
    public void updateUserLevel(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Long reviewCount = reviewRepository.countByUserId(userId);

        // 조건을 만족하는 레벨 목록 중 가장 높은 레벨 선택
        List<UserLevel> levels = userLevelRepository.findApplicableLevels(reviewCount);
        if (!levels.isEmpty()) {
            user.setUserLevel(levels.get(0));
        }
    }
}

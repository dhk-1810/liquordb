package com.liquordb.service;

import com.liquordb.dto.user.*;
import com.liquordb.repository.CommentLikeRepository;
import com.liquordb.repository.LiquorLikeRepository;
import com.liquordb.repository.ReviewLikeRepository;
import com.liquordb.dto.liquor.LiquorSummaryDto;

import com.liquordb.repository.LiquorRepository;
import com.liquordb.repository.LiquorTagRepository;
import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.dto.review.ReviewSummaryDto;
import com.liquordb.entity.User;
import com.liquordb.entity.UserStatus;
import com.liquordb.repository.UserRepository;
import com.liquordb.repository.ReviewRepository;
import com.liquordb.repository.CommentRepository;

import com.liquordb.repository.UserTagRepository;
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
    private final LiquorTagRepository liquorTagRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final LiquorRepository liquorRepository;
    private final LiquorLikeRepository liquorLikeRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final CommentLikeRepository commentLikeRepository;

    private final TagService tagService;
    private final FileUploadService fileUploadService;

    // 회원가입
    public UserResponseDto register(UserRegisterRequestDto dto) {
        if (userRepository.existsByEmailAndStatusNot(dto.getEmail(), UserStatus.WITHDRAWN)) {
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

        User user = userRepository.findByEmailAndStatusNot(email, UserStatus.WITHDRAWN)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(pw, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return UserLoginResponseDto.from(user);
    }

    // 비밀번호 찾기 (임시 비밀번호 발급)
    public void findPasswordAndSend(UserFindPasswordRequestDto dto) {
        User user = userRepository.findByEmailAndStatusNot(dto.getEmail(), UserStatus.WITHDRAWN)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 사용자가 없습니다."));

        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        // 이메일 전송
        sendTempPassword(user.getEmail(), tempPassword);
    }

    // 회원 탈퇴 (soft delete)
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new IllegalStateException("이미 탈퇴한 사용자입니다.");
        }

        user.setStatus(UserStatus.WITHDRAWN);
        userRepository.save(user);
    }

    // 마이페이지
    public UserMyPageResponseDto getMyPageInfo(UUID userId, boolean showAllTags) {
        User user = userRepository.findByIdAndStatusNot(userId, UserStatus.WITHDRAWN)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        long reviewCount = reviewRepository.countByUserId(userId);
        long commentCount = commentRepository.countByUserId(userId);

        long likedLiquorCount = liquorLikeRepository.countByUserId(userId);
        long likedReviewCount = reviewLikeRepository.countByUserId(userId);
        long likedCommentCount = commentLikeRepository.countByUserId(userId);

        // 리뷰 작성 목록
        List<ReviewSummaryDto> reviewList = reviewRepository.findByUserId(userId).stream()
                .map(ReviewSummaryDto::from)
                .toList();

        // 댓글 작성 목록
        List<CommentResponseDto> commentList = commentRepository.findByUserId(userId).stream()
                .map(comment -> CommentResponseDto.from(
                        comment,
                        commentLikeRepository.countByCommentId(comment.getId())
                ))
                .toList();

        // 좋아요한 주류 목록
        List<LiquorSummaryDto> likedLiquors = liquorLikeRepository.findByUserId(userId).stream()
                .map(like -> liquorRepository.findById(like.getLiquor().getId())
                        .map(LiquorSummaryDto::from)
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        // 좋아요한 리뷰 목록
        List<ReviewSummaryDto> likedReviews = reviewLikeRepository.findByUserId(userId).stream()
                .map(like -> reviewRepository.findById(like.getReview().getId())
                        .map(ReviewSummaryDto::from)
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        // 좋아요한 댓글 목록 (likeCount 포함)
        List<CommentResponseDto> likedComments = commentLikeRepository.findByUserId(userId).stream()
                .map(like -> commentRepository.findById(like.getComment().getId())
                        .map(comment -> CommentResponseDto.from(
                                comment,
                                commentLikeRepository.countByCommentId(comment.getId())
                        ))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        //
        // 등록한(=선호하는) 태그 목록
        List<String> preferredTags = tagService.getPreferredTagsForUser(userId, showAllTags);


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
                .reviewList(reviewList)
                .commentList(commentList)
                .preferredTags(preferredTags)
                .build();
    }

    // 회원정보수정 (프사, 닉네임)
    public void update(UUID userId, UserUpdateRequestDto dto) {
        User user = userRepository.findByIdAndStatusNot(userId, UserStatus.WITHDRAWN)
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
    public void updatePassword(UUID userId, UserUpdatePasswordDto dto) {
        User user = userRepository.findByIdAndStatusNot(userId, UserStatus.WITHDRAWN)
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
}


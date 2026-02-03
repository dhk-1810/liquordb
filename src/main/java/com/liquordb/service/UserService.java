package com.liquordb.service;

import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.dto.user.*;
import com.liquordb.entity.*;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    // 회원 탈퇴 (soft delete)
    @Transactional
    @PreAuthorize("#userId == authentication.principal.userId")
    public void withdraw(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.withdraw();
        userRepository.save(user);
    }

    // 마이페이지
    @Transactional
    @PreAuthorize("#userId == authentication.principal.userId")
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
                .map(CommentMapper::toDto)
                .toList();
        long reviewCount = createdReviews.size();
        long commentCount = createdComments.size();

        // 좋아요한 주류, 리뷰, 댓글 목록
        List<LiquorLike> liquorLikes = liquorLikeRepository.findByUser_IdAndLiquor_IsDeletedFalse(userId); // TODO 개선
        long liquorLikeCount = liquorLikes.size();
        List<LiquorSummaryDto> likedLiquors = liquorLikes.stream()
                .map(liquorLike -> LiquorMapper.toSummaryDto(liquorLike.getLiquor(), true, reviewCount, liquorLikeCount))
                .toList();
        List<ReviewResponseDto> likedReviews = reviewLikeRepository.findByUser_IdAndReviewIsHiddenFalse(userId).stream()
                .map(reviewLike -> ReviewMapper.toDto(reviewLike.getReview()))
                .toList();
        List<CommentResponseDto> likedComments = commentLikeRepository.findByUser_IdAndCommentIsHiddenFalse(userId).stream()
                .map(commentLike -> CommentMapper.toDto(commentLike.getComment()))
                .toList();


        long likedLiquorCount = likedLiquors.size();
        long likedReviewCount = likedReviews.size();
        long likedCommentCount = likedComments.size();


        // 등록한(=선호하는) 태그 목록
        List<TagResponseDto> preferredTags = userTagService.getByUserId(userId, showAllTags);

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
    @PreAuthorize("#userId == authentication.principal.userId")
    public void update(UUID userId, UserUpdateRequestDto request, MultipartFile newProfileImage) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String email = request.email();
        if (email != null && !userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
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

    // 비밀번호 수정 (로그인 상태에서)
    @Transactional
    public void updatePassword(PasswordUpdateRequestDto request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        user.updatePassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

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

}


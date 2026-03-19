package com.liquordb.service;

import com.liquordb.dto.FileResponseDto;
import com.liquordb.dto.PageResponse;
import com.liquordb.dto.user.*;
import com.liquordb.entity.*;
import com.liquordb.enums.Role;
import com.liquordb.enums.SortDirection;
import com.liquordb.enums.UserStatus;
import com.liquordb.exception.user.*;
import com.liquordb.mapper.UserMapper;
import com.liquordb.repository.*;

import com.liquordb.repository.comment.CommentRepository;
import com.liquordb.repository.review.ReviewRepository;
import com.liquordb.repository.user.UserRepository;
import com.liquordb.repository.user.UserSearchCondition;
import com.liquordb.security.JwtInformation;
import com.liquordb.security.JwtTokenProvider;
import com.liquordb.security.RedisJwtRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final LiquorLikeRepository liquorLikeRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final FileService fileService; // 단방향 참조
    private final S3Service s3Service; // 단방향 참조
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisJwtRegistry jwtRegistry;

    private final PasswordEncoder passwordEncoder;

    // 마이페이지
    @Transactional(readOnly = true)
    public UserMyPageDto getMyPageInfo(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 마이페이지 조회 횟수가 많지 않을 것이라 판단,
        // 매 활동마다 User 엔터티의 count 정보도 업데이트 하지 않고 리포지토리 직접 조회.
        long reviewCount = reviewRepository.countByUser_IdAndStatus(userId, Review.ReviewStatus.ACTIVE);
        long commentCount = commentRepository.countByUser_IdAndStatus(userId, Comment.CommentStatus.ACTIVE);

        long likedLiquorCount = liquorLikeRepository.countByUser_IdAndLiquorIsDeletedFalse(userId);
        long likedReviewCount = reviewLikeRepository.countByUser_IdAndReviewStatus(userId, Review.ReviewStatus.ACTIVE);
        long likedCommentCount = commentLikeRepository.countByUser_IdAndCommentStatus(userId, Comment.CommentStatus.ACTIVE);

        String imageUrl = s3Service.getProfileImageUrl(user.getProfileImageKey()); // null-safe 메서드.
        return UserMapper.toMyPageDto(
                user,
                imageUrl,
                reviewCount,
                commentCount,
                likedLiquorCount,
                likedReviewCount,
                likedCommentCount
        );
    }

    // 회원정보수정 (닉네임, 프사)
    @Transactional
    public JwtInformation update(
            UUID userId,
            UserUpdateRequest request,
            MultipartFile profileImage,
            String refreshToken
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        boolean isIdentifierChanged = false;

        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) throw new DuplicateEmailException(request.email());
            user.updateEmail(request.email());
            isIdentifierChanged = true;
        }

        if (request.username() != null && !request.username().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.username())) throw new DuplicateUsernameException(request.username());
            user.updateUsername(request.username());
            isIdentifierChanged = true;
        }

        Boolean deleteImage = request.deleteProfileImage();
        if (deleteImage != null && deleteImage) {
            // TODO fileService.delete(user.getProfileImageKey());
            user.setProfileImage(null);
        }

        if (profileImage != null && !profileImage.isEmpty()) {
            FileResponseDto file = fileService.upload(profileImage, File.FileType.PROFILE, userId);
            user.setProfileImage(file.key());
        }

        userRepository.save(user);

        String newAccess = null;
        String newRefresh = null;
        if (isIdentifierChanged) {
            newAccess = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole().name());
            newRefresh = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getRole().name());
            jwtRegistry.rotateRefreshToken(refreshToken, newRefresh, userId);
        }

        return new JwtInformation(UserMapper.toDto(user), newAccess, newRefresh);
    }

    // 비밀번호 수정 (로그인 상태에서)
    @Transactional
    public void updatePassword(UUID userId, PasswordUpdateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        user.updatePassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    // 회원 탈퇴 (soft delete)
    @Transactional
    public void withdraw(UUID userId, String accessToken) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.withdraw();
        userRepository.save(user);
        jwtRegistry.invalidateAllRefreshTokensByUserId(user.getId());
        long remainingMs = jwtTokenProvider.getRemainingExpiration(accessToken);
        jwtRegistry.addToBlacklist(accessToken, remainingMs);
    }

    /**
     * 관리자용 메서드
     */

    @Transactional
    public JwtInformation updateRole(Role role, UUID userId, String refreshToken) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String newAccess = null;
        String newRefresh = null;

        if (!role.equals(user.getRole())) {
            user.updateRole(role);
            userRepository.save(user);
            newAccess = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole().name());
            newRefresh = jwtTokenProvider.createRefreshToken(user.getEmail(), user.getRole().name());
            jwtRegistry.rotateRefreshToken(refreshToken, newRefresh, userId);
        }
        return new JwtInformation(UserMapper.toDto(user), newAccess, newRefresh);
    }

    // 유저 조회 - 전체 또는 검색
    @Transactional(readOnly = true)
    public PageResponse<UserResponseDto> getAll(UserListGetRequest request) {

        UserStatus status = request.status() == null
                ? UserStatus.ACTIVE
                : request.status();
        int page = request.page() == null
                ? 0
                : request.page();
        int limit = request.limit() == null
                ? 50
                : request.limit();
        boolean descending = request.sortDirection() == SortDirection.DESC;
        UserSearchCondition condition = new UserSearchCondition(request.username(), request.email(), status, page, limit, descending);

        Page<UserResponseDto> data = userRepository.findAll(condition)
                .map(UserMapper::toDto);

        return PageResponse.from(data);
    }
}


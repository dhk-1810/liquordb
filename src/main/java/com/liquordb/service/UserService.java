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
    private final FileService fileService;
    private final S3Service s3Service;

    private final PasswordEncoder passwordEncoder;

    // 마이페이지
    @Transactional
    public UserMyPageDto getMyPageInfo(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!user.getId().equals(userId)) {
            throw new UserAccessDeniedException(userId);
        }

        long reviewCount = reviewRepository.countByUser_IdAndStatus(userId, Review.ReviewStatus.ACTIVE);
        long commentCount = commentRepository.countByUser_IdAndStatus(userId, Comment.CommentStatus.ACTIVE);

        long likedLiquorCount = liquorLikeRepository.countByUser_IdAndLiquorIsDeletedFalse(userId);
        long likedReviewCount = reviewLikeRepository.countByUser_IdAndReviewStatus(userId, Review.ReviewStatus.ACTIVE);
        long likedCommentCount = commentLikeRepository.countByUser_IdAndCommentStatus(userId, Comment.CommentStatus.ACTIVE);

        // 등록한(=선호하는) 태그 목록

        String presignedUrl = s3Service.createPresignedUrl(user.getProfileImageKey());
        return UserMapper.toMyPageDto(
                user,
                presignedUrl,
                reviewCount,
                commentCount,
                likedLiquorCount,
                likedReviewCount,
                likedCommentCount
        );
    }

    // 회원정보수정 (닉네임, 프사)
    @Transactional
    public void update(UUID userId, UserUpdateRequest request, MultipartFile profileImage) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!user.getId().equals(userId)) {
            throw new UserAccessDeniedException(userId);
        }

        String email = request.email();
        if (email != null && !userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }

        String username = request.username();
        if (username != null && !userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException(username);
        }

        if (request.deleteProfileImage()) {
            // TODO fileService.delete(user.getProfileImageKey());
            user.setProfileImage(null);
        }

        if (profileImage != null && !profileImage.isEmpty()) {
            FileResponseDto file = fileService.upload(profileImage, File.FileType.PROFILE);
            user.setProfileImage(file.key());
        }

        userRepository.save(user);
    }

    // 비밀번호 수정 (로그인 상태에서)
    @Transactional
    public void updatePassword(UUID userId, PasswordUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (!user.getId().equals(userId)) {
            throw new UserAccessDeniedException(userId);
        }

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        user.updatePassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    // 회원 탈퇴 (soft delete)
    @Transactional
    public void withdraw(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (!user.getId().equals(userId)) {
            throw new UserAccessDeniedException(userId);
        }
        user.withdraw();
        userRepository.save(user);
    }

    /**
     * 관리자용 메서드
     */

    @Transactional
    public void updateRole(Role role, UUID userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.updateRole(role);
        userRepository.save(user);
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


package com.liquordb.service;

import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.dto.user.*;
import com.liquordb.entity.*;
import com.liquordb.enums.UserStatus;
import com.liquordb.exception.user.*;
import com.liquordb.mapper.TagMapper;
import com.liquordb.mapper.UserMapper;
import com.liquordb.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final LiquorLikeRepository liquorLikeRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserTagRepository userTagRepository;
    private final FileService fileService;

    private final PasswordEncoder passwordEncoder;

    // 마이페이지
    @Transactional
    @PreAuthorize("#userId == authentication.principal.userId")
    public UserMyPageDto getMyPageInfo(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        long reviewCount = reviewRepository.countByUser_IdAndStatus(userId, Review.ReviewStatus.ACTIVE);
        long commentCount = commentRepository.countByUser_IdAndStatus(userId, Comment.CommentStatus.ACTIVE);

        long likedLiquorCount = liquorLikeRepository.countByUser_IdAndLiquorIsDeletedFalse(userId);
        long likedReviewCount = reviewLikeRepository.countByUser_IdAndReviewStatus(userId, Review.ReviewStatus.ACTIVE);
        long likedCommentCount = commentLikeRepository.countByUser_IdAndCommentStatus(userId, Comment.CommentStatus.ACTIVE);

        // 등록한(=선호하는) 태그 목록
        List<TagResponseDto> preferredTags = userTagRepository.findAllByUser_IdWithTag(userId)
                .stream().map(TagMapper::toDto).toList();

        return UserMapper.toMyPageDto(
                user,
                reviewCount,
                commentCount,
                likedLiquorCount,
                likedReviewCount,
                likedCommentCount,
                preferredTags
        );
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
    @PreAuthorize("#userId == authentication.principal.userId")
    public void updatePassword(UUID userId, PasswordUpdateRequestDto request) {
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
    @PreAuthorize("#userId == authentication.principal.userId")
    public void withdraw(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.withdraw();
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


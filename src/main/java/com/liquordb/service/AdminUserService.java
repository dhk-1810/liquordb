package com.liquordb.service;

import com.liquordb.repository.CommentLikeRepository;
import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.repository.CommentRepository;
import com.liquordb.repository.ReviewRepository;
import com.liquordb.dto.user.UserAdminDto;
import com.liquordb.entity.User;
import com.liquordb.entity.UserStatus;
import com.liquordb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 관리자용 유저서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    // 유저 조회 - 전체 또는 검색
    public List<UserAdminDto> searchUsers(String keyword, UserStatus status) {
        if ((keyword == null || keyword.isBlank()) && status == null) {
            return userRepository.findAll().stream()
                    .map(UserAdminDto::from)
                    .toList();
        }

        // 조건 검색이 가능하도록 사용자 정의 메서드 또는 Specification 사용
        return userRepository.search(keyword, status).stream()
                .map(UserAdminDto::from)
                .toList();
    }

    // 리뷰 조회
    public List<ReviewResponseDto> getUserReviews(UUID userId) {
        return reviewRepository.findByUserId(userId).stream()
                .map(ReviewResponseDto::from)  // 정적 메서드 또는 생성자 필요
                .toList();
    }

    // 댓글 조회
    public List<CommentResponseDto> getUserComments(UUID userId) {
        return commentRepository.findByUserId(userId).stream()
                .map(comment -> {
                    long likeCount = commentLikeRepository.countByCommentId(comment.getId());
                    return CommentResponseDto.from(comment, likeCount);
                })
                .toList();
    }

    // 유저 이용제한
    public void restrictUser(UUID userId, String period) {
        User user = userRepository.findById(userId)
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

        userRepository.save(user);
    }
}
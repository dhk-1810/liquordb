package com.liquordb.service;

import com.liquordb.dto.comment.CommentRequestDto;
import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.entity.Comment;
import com.liquordb.repository.CommentRepository;
import com.liquordb.entity.Review;
import com.liquordb.repository.ReviewRepository;
import com.liquordb.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;

    // 댓글 생성
    public void createComment(User user, CommentRequestDto dto) {
        Review review = reviewRepository.findById(dto.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        Comment parent = null;
        if (dto.getParentId() != null) {
            parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
        }

        Comment comment = Comment.builder()
                .user(user)
                .review(review)
                .parent(parent)
                .content(dto.getContent())
                .isDeleted(false)
                .build();

        commentRepository.save(comment);
    }

    // 댓글 삭제 (소프트 삭제)
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalStateException("본인의 댓글만 삭제할 수 있습니다.");
        }

        // 원댓글이 삭제되어도 자식댓글이 있으면 내용을 남김
        boolean hasChildren = commentRepository.existsByParentId(commentId);

        if (hasChildren) {
            comment.setContent("삭제된 댓글입니다.");
        }

        comment.setIsDeleted(true);
    }

    // 특정 리뷰의 댓글 전체 조회
    public List<CommentResponseDto> getCommentsByReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        return commentRepository.findAllByReviewAndIsDeletedFalse(review);
    }
}

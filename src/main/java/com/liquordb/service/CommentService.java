package com.liquordb.service;

import com.liquordb.PageResponse;
import com.liquordb.dto.comment.CommentRequestDto;
import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.dto.comment.CommentUpdateRequestDto;
import com.liquordb.entity.Comment;
import com.liquordb.exception.NotFoundException;
import com.liquordb.mapper.CommentMapper;
import com.liquordb.repository.CommentRepository;
import com.liquordb.entity.Review;
import com.liquordb.repository.ReviewRepository;
import com.liquordb.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;

    // 댓글 생성
    @Transactional
    public CommentResponseDto create(User user, CommentRequestDto dto) {
        Review review = reviewRepository.findById(dto.getReviewId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리뷰입니다."));

        Comment parent = null;
        if (dto.getParentId() != null) { // 대댓글일 경우
            parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new NotFoundException("존재하지 않는 부모댓글입니다."));
        }

        Comment comment = Comment.builder()
                .user(user)
                .review(review)
                .parent(parent)
                .content(dto.getContent())
                .isDeleted(false)
                .build();

        return CommentMapper.toDto(commentRepository.save(comment));
    }

    // 댓글 수정
    @Transactional
    public CommentResponseDto update(User user, Long commentId, CommentUpdateRequestDto dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 댓글입니다."));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("권한이 없습니다. 댓글 수정은 댓글 작성자만 가능합니다.");
        }

        if (comment.isDeleted()) {
            throw new IllegalStateException("삭제된 댓글은 수정할 수 없습니다.");
        }

        comment.setContent(dto.getContent());
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    // 댓글 삭제 (소프트 삭제)
    @Transactional
    public CommentResponseDto delete(Long commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 댓글입니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalStateException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        comment.setDeleted(true);
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    // 특정 리뷰의 댓글 전체 조회
    @Transactional(readOnly = true)
    public PageResponse<CommentResponseDto> getCommentsByReview(Long reviewId, Pageable pageable) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리뷰입니다."));

        Page<Comment> commentPage = commentRepository.findAllByReviewAndIsDeletedFalse(review, pageable);
        Page<CommentResponseDto> dtoPage = commentPage.map(CommentMapper::toDto);

        return PageResponse.from(dtoPage);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsByUser(Pageable pageable, UUID userId) {

        Page<Comment> comments = commentRepository.findByUserIdAndIsDeletedFalse(pageable, userId);

        return comments.stream()
                .map(CommentMapper::toDto)
                .toList();
    }
}

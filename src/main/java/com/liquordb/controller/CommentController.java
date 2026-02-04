package com.liquordb.controller;

import com.liquordb.PageResponse;
import com.liquordb.dto.comment.CommentLikeResponseDto;
import com.liquordb.dto.comment.CommentRequestDto;
import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.dto.comment.CommentUpdateRequestDto;
import com.liquordb.security.CustomUserDetails;
import com.liquordb.service.CommentLikeService;
import com.liquordb.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;
    private final CommentLikeService commentLikeService;

    // 댓글 작성
    @PostMapping("/reviews/{reviewId}/comments")
    public ResponseEntity<CommentResponseDto> create(
            @PathVariable Long reviewId,
            @RequestBody @Valid CommentRequestDto request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        CommentResponseDto response = commentService.create(reviewId, request, user.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 특정 리뷰의 댓글 조회
    @GetMapping("/reviews/{reviewId}/comments")
    public ResponseEntity<PageResponse<CommentResponseDto>> findByReviewId(
            @PathVariable Long reviewId,
            Pageable pageable
    ) {
        PageResponse<CommentResponseDto> comments = commentService.findByReviewId(reviewId, pageable);
        return ResponseEntity.ok(comments);
    }

    // 댓글 수정
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> update(
            @PathVariable Long commentId,
            @RequestBody @Valid CommentUpdateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        CommentResponseDto response = commentService.update(commentId, request, user.getUserId());
        return ResponseEntity.ok(response);
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        commentService.deleteByIdAndUser(commentId, user.getUserId());
        return ResponseEntity.noContent().build();
    }

    // 좋아요 토글
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<CommentLikeResponseDto> toggleLike(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        CommentLikeResponseDto response = commentLikeService.toggleLike(user.getUserId(), commentId);
        return ResponseEntity.ok(response);
    }

}

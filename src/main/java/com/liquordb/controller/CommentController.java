package com.liquordb.controller;

import com.liquordb.PageResponse;
import com.liquordb.dto.comment.CommentLikeResponseDto;
import com.liquordb.dto.comment.CommentRequestDto;
import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.dto.comment.CommentUpdateRequestDto;
import com.liquordb.service.CommentLikeService;
import com.liquordb.service.CommentService;
import com.liquordb.UserValidator;
import com.liquordb.entity.User;
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
    private final UserValidator userValidator;

    // 댓글 작성
    @PostMapping("/reviews/{reviewId}/comments")
    public ResponseEntity<CommentResponseDto> create(@PathVariable Long reviewId,
                                                     @RequestBody @Valid CommentRequestDto request,
                                                     @AuthenticationPrincipal User user
    ) {
        userValidator.validateCanPost(user);
        CommentResponseDto response = commentService.create(user, reviewId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 특정 리뷰의 댓글 조회
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<PageResponse<CommentResponseDto>> findByReviewId(@PathVariable Long reviewId,
                                                                           Pageable pageable) {
        PageResponse<CommentResponseDto> comments = commentService.findByReviewId(reviewId, pageable);
        return ResponseEntity.ok(comments);
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> update(@PathVariable Long commentId,
                                                     @RequestBody @Valid CommentUpdateRequestDto request,
                                                     @AuthenticationPrincipal User user){
        CommentResponseDto response = commentService.update(user, commentId, request);
        return ResponseEntity.ok(response);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId,
                                       @AuthenticationPrincipal User user) {
        commentService.deleteByIdAndUser(commentId, user);
        return ResponseEntity.noContent().build();
    }

    // 좋아요 토글
    @PostMapping("/{commentId}/like")
    public ResponseEntity<CommentLikeResponseDto> toggleLike(@PathVariable Long commentId,
                                                             @RequestParam UUID userId) {
        CommentLikeResponseDto response = commentLikeService.toggleLike(userId, commentId);
        return ResponseEntity.ok(response);
    }

}

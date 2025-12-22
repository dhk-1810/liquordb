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
import com.liquordb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentLikeService commentLikeService;
    private final UserValidator userValidator;

    // 댓글 작성
    @PostMapping
    public ResponseEntity<String> create(
            @RequestBody CommentRequestDto dto,
            @AuthenticationPrincipal User user
    ) {
        userValidator.validateCanPost(user);
        commentService.create(user, dto);
        return ResponseEntity.ok("댓글이 등록되었습니다.");
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
                                                     @RequestBody CommentUpdateRequestDto dto,
                                                     @AuthenticationPrincipal User user){
        CommentResponseDto response = commentService.update(user, commentId, dto);
        return ResponseEntity.ok(response);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> delete(@PathVariable Long commentId,
                                         @AuthenticationPrincipal User user) {
        commentService.deleteByIdAndUser(commentId, user);
        return ResponseEntity.ok("댓글이 삭제되었습니다.");
    }

    // 좋아요 토글
    @PostMapping("/{commentId}/like")
    public ResponseEntity<CommentLikeResponseDto> toggleLike(@PathVariable Long commentId,
                                                             @RequestParam UUID userId) {
        CommentLikeResponseDto response = commentLikeService.toggleLike(userId, commentId);
        return ResponseEntity.ok(response);
    }

}

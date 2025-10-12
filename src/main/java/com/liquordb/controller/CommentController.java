package com.liquordb.controller;

import com.liquordb.dto.comment.CommentRequestDto;
import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.service.CommentService;
import com.liquordb.user.UserValidator;
import com.liquordb.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserValidator userValidator;

    // 댓글 작성
    @PostMapping
    public ResponseEntity<String> createComment(
            @RequestBody CommentRequestDto dto,
            @AuthenticationPrincipal User user
    ) {
        userValidator.validateCanPost(user);
        commentService.createComment(user, dto);
        return ResponseEntity.ok("댓글이 등록되었습니다.");
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal User user) {
        commentService.deleteComment(commentId, user.getId());
        return ResponseEntity.ok("댓글이 삭제되었습니다.");
    }

    // 특정 리뷰의 댓글 조회
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByReview(@PathVariable Long reviewId) {
        List<CommentResponseDto> comments = commentService.getCommentsByReview(reviewId);
        return ResponseEntity.ok(comments);
    }
}

package com.liquordb.controller;

import com.liquordb.dto.comment.CommentLikeResponseDto;
import com.liquordb.service.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/comment-likes")
@RequiredArgsConstructor
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    // 댓글 좋아요 토글 (누르기/취소)
    @PostMapping("/{userId}/toggle")
    public ResponseEntity<CommentLikeResponseDto> toggleLike(@PathVariable UUID userId,
                                                             @RequestParam Long commentId) {
        CommentLikeResponseDto response = commentLikeService.toggleLike(userId, commentId);
        return ResponseEntity.ok(response);
    }

    // 댓글 좋아요 카운트
    @GetMapping("/count")
    public ResponseEntity<Long> countLikes(@RequestParam Long commentId) {
        long count = commentLikeService.countLikes(commentId);
        return ResponseEntity.ok(count);
    }
}

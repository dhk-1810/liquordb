package com.liquordb.like.controller;

import com.liquordb.like.dto.CommentLikeResponseDto;
import com.liquordb.like.service.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment-likes")
@RequiredArgsConstructor
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    // 댓글 좋아요 토글 (누르기/취소)
    @PostMapping("/{userId}/toggle")
    public ResponseEntity<CommentLikeResponseDto> toggleLike(@PathVariable Long userId,
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

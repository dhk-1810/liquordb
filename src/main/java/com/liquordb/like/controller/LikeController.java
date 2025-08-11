package com.liquordb.like.controller;

import com.liquordb.like.dto.LikeRequestDto;
import com.liquordb.like.dto.LikeResponseDto;
import com.liquordb.like.entity.LikeTargetType;
import com.liquordb.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    // 좋아요 누르기
    @PostMapping("/{userId}")
    public ResponseEntity<LikeResponseDto> like(@PathVariable Long userId,
                                                @RequestBody LikeRequestDto request) {
        return ResponseEntity.ok(likeService.like(userId, request));
    }

    // 좋아요 취소
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> unlike(@PathVariable Long userId,
                                       @RequestBody LikeRequestDto request) {
        likeService.unlike(userId, request);
        return ResponseEntity.noContent().build();
    }

    // 좋아요 카운트 (주류, 리뷰, 댓글)
    @GetMapping("/count")
    public ResponseEntity<Long> countLikes(@RequestParam Long targetId,
                                           @RequestParam LikeTargetType targetType) {

        long count = likeService.countLikes(targetId, targetType);
        return ResponseEntity.ok(count);
    }
}

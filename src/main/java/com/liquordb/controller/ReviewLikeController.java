package com.liquordb.controller;

import com.liquordb.dto.review.ReviewLikeResponseDto;
import com.liquordb.service.ReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review-likes")
@RequiredArgsConstructor
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;

    // 리뷰 좋아요 토글 (누르기/취소)
    @PostMapping("/{userId}/toggle")
    public ResponseEntity<ReviewLikeResponseDto> toggleLike(@PathVariable Long userId,
                                                            @RequestParam Long reviewId) {
        ReviewLikeResponseDto response = reviewLikeService.toggleLike(userId, reviewId);
        return ResponseEntity.ok(response);
    }

    // 리뷰 좋아요 카운트
    @GetMapping("/review-count")
    public ResponseEntity<Long> countLikes(@RequestParam Long reviewId) {
        long count = reviewLikeService.countLikes(reviewId);
        return ResponseEntity.ok(count);
    }
}

package com.liquordb.controller;

import com.liquordb.dto.liquor.LiquorLikeResponseDto;
import com.liquordb.service.LiquorLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/liquor-likes")
@RequiredArgsConstructor
public class LiquorLikeController {

    private final LiquorLikeService liquorLikeService;

    // 주류 좋아요 토글 (누르기/취소)
    @PostMapping("/{userId}/toggle")
    public ResponseEntity<LiquorLikeResponseDto> toggleLike(@PathVariable UUID userId,
                                                            @RequestBody Long liquorId) {
        LiquorLikeResponseDto response = liquorLikeService.toggleLike(userId, liquorId);
        return ResponseEntity.ok(response);
    }

    // 주류 좋아요 카운트
    @GetMapping("/liquor-count")
    public ResponseEntity<Long> countLikes(@RequestParam Long liquorId) {
        long count = liquorLikeService.countByLiquorIdAndLikedTrue(liquorId);
        return ResponseEntity.ok(count);
    }
}

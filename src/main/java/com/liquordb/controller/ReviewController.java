package com.liquordb.controller;

import com.liquordb.dto.review.ReviewLikeResponseDto;
import com.liquordb.dto.review.ReviewRequestDto;
import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.dto.review.ReviewUpdateRequestDto;
import com.liquordb.security.CustomUserDetails;
import com.liquordb.service.ReviewLikeService;
import com.liquordb.service.ReviewService;
import com.liquordb.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewLikeService reviewLikeService;

    // 리뷰 등록
    @PostMapping("/liquors/{liquorId}/reviews")
    public ResponseEntity<ReviewResponseDto> create(
            @PathVariable Long liquorId,
            @RequestPart @Valid ReviewRequestDto requestDto,
            @RequestPart(required = false) List<MultipartFile> images,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        ReviewResponseDto response = reviewService.create(liquorId, requestDto, images, user.getUserId());
        return ResponseEntity.ok(response);
    }

    // 리뷰 수정
    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponseDto> update(
            @PathVariable Long reviewId,
            @RequestPart @Valid ReviewUpdateRequestDto request,
            @RequestPart(required = false) List<MultipartFile> imagesToAdd,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        ReviewResponseDto response = reviewService.update(reviewId, request, imagesToAdd, user.getUserId());
        return ResponseEntity.ok(response);
    }

    // 리뷰 삭제
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> delete(@PathVariable Long reviewId, @AuthenticationPrincipal CustomUserDetails user) {
        reviewService.delete(reviewId, user.getUserId());
        return ResponseEntity.noContent().build();
    }

    // 리뷰 좋아요
    @PostMapping("/reviews/{reviewId}/like")
    public ResponseEntity<ReviewLikeResponseDto> toggleLike(@PathVariable Long reviewId,
                                                            @AuthenticationPrincipal CustomUserDetails user) {
        ReviewLikeResponseDto response = reviewLikeService.toggleLike(user.getUserId(), reviewId);
        return ResponseEntity.ok(response);
    }

}

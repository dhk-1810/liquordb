package com.liquordb.controller;

import com.liquordb.dto.CursorPageResponse;
import com.liquordb.dto.LikeResponseDto;
import com.liquordb.dto.review.*;
import com.liquordb.security.CustomUserDetails;
import com.liquordb.service.ReviewLikeService;
import com.liquordb.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewLikeService reviewLikeService;

    // 리뷰 등록
    @PostMapping("/liquors/{liquorId}/reviews")
    public ResponseEntity<ReviewResponseDto> create(
            @PathVariable Long liquorId,
            @RequestPart @Valid ReviewRequest requestDto,
            @RequestPart(required = false) List<MultipartFile> images,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        ReviewResponseDto response = reviewService.create(liquorId, requestDto, images, user.getUserId());
        return ResponseEntity.ok(response);
    }

    // 리뷰 목록 조회 - 주류별
    @GetMapping("/liquors/{liquorId}/reviews")
    public ResponseEntity<CursorPageResponse<ReviewResponseDto>> getReviewsByLiquor(
            @PathVariable Long liquorId,
            @ModelAttribute @Valid ReviewListGetRequest request
    ) {
        CursorPageResponse<ReviewResponseDto> response = reviewService.getAllByLiquorId(liquorId, request);
        return ResponseEntity.ok(response);
    }

    // 리뷰 목록 조회 - 유저별
    @GetMapping("/users/{authorId}/reviews")
    public ResponseEntity<CursorPageResponse<ReviewResponseDto>> getReviewsByUser(
            @PathVariable UUID authorId,
            @ModelAttribute @Valid ReviewListGetRequest request
    ) {
        CursorPageResponse<ReviewResponseDto> response = reviewService.getAllByUserId(authorId, request);
        return ResponseEntity.ok(response);
    }

    // 리뷰 수정
    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponseDto> update(
            @PathVariable Long reviewId,
            @RequestPart @Valid ReviewUpdateRequest request,
            @RequestPart(required = false) List<MultipartFile> imagesToAdd,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        ReviewResponseDto response = reviewService.update(reviewId, request, imagesToAdd, user.getUserId());
        return ResponseEntity.ok(response);
    }

    // 리뷰 삭제
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        reviewService.delete(reviewId, user.getUserId());
        return ResponseEntity.noContent().build();
    }

    // 좋아요
    @PostMapping("/reviews/{reviewId}/like")
    public ResponseEntity<LikeResponseDto> like(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        LikeResponseDto response = reviewLikeService.like(reviewId, user.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 좋아요 취소
    @DeleteMapping("/reviews/{reviewId}/cancel-like")
    public ResponseEntity<LikeResponseDto> cancelLike(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        LikeResponseDto response = reviewLikeService.cancelLike(reviewId, user.getUserId());
        return ResponseEntity.ok(response);
    }

}

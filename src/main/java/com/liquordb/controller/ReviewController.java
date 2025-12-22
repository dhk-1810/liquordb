package com.liquordb.controller;

import com.liquordb.dto.review.ReviewLikeResponseDto;
import com.liquordb.dto.review.ReviewRequestDto;
import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.dto.review.ReviewUpdateRequestDto;
import com.liquordb.service.ReviewLikeService;
import com.liquordb.service.ReviewService;
import com.liquordb.UserValidator;
import com.liquordb.entity.User;
import com.liquordb.service.UserService;
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
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewLikeService reviewLikeService;
    private final UserValidator userValidator;

    // 리뷰 등록
    @PostMapping
    public ResponseEntity<ReviewResponseDto> create(
            @RequestPart @Valid ReviewRequestDto requestDto,
            @RequestPart(required = false) List<MultipartFile> images,
            @AuthenticationPrincipal User user
    ) {
        userValidator.validateCanPost(user);
        ReviewResponseDto response = reviewService.create(user, requestDto, images);
        return ResponseEntity.ok(response);
    }

    // 리뷰 수정
    @PatchMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> update(
            @PathVariable Long reviewId,
            @RequestPart @Valid ReviewUpdateRequestDto request,
            @RequestPart(required = false) List<MultipartFile> imagesToAdd,
            @AuthenticationPrincipal User user
    ) {
        userValidator.validateCanPost(user);
        ReviewResponseDto response = reviewService.update(reviewId, user, request, imagesToAdd);
        return ResponseEntity.ok(response);
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(@PathVariable Long reviewId, @AuthenticationPrincipal User user) {
        userValidator.validateCanPost(user);
        reviewService.deleteByIdAndUser(reviewId, user);
        return ResponseEntity.noContent().build();
    }

    // 리뷰 좋아요
    // TODO 유저 인증정보
    @PostMapping("/{reviewId}/like")
    public ResponseEntity<ReviewLikeResponseDto> toggleLike(@PathVariable Long reviewId,
                                                            @RequestParam UUID userId) {
        ReviewLikeResponseDto response = reviewLikeService.toggleLike(userId, reviewId);
        return ResponseEntity.ok(response);
    }

}

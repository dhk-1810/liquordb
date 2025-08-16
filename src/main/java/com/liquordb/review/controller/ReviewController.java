package com.liquordb.review.controller;

import com.liquordb.review.dto.ReviewRequestDto;
import com.liquordb.review.dto.ReviewResponseDto;
import com.liquordb.review.service.ReviewService;
import com.liquordb.user.UserValidator;
import com.liquordb.user.entity.User;
import com.liquordb.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private ReviewService reviewService;
    private UserValidator userValidator;

    // 리뷰 등록
    @PostMapping
    public ResponseEntity<?> createReview(
            @RequestBody @Valid ReviewRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {

        userValidator.validateCanPost(user);
        ReviewResponseDto response = reviewService.createReview(user, requestDto);
        return ResponseEntity.ok(response);
    }

    // 리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        userValidator.validateCanPost(user);
        ReviewResponseDto response = reviewService.updateReview(reviewId, user, requestDto);
        return ResponseEntity.ok(response);
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User user
    ) {
        userValidator.validateCanPost(user);
        reviewService.deleteReview(reviewId, user);
        return ResponseEntity.noContent().build();
    }
}

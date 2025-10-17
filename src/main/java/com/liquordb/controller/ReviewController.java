package com.liquordb.controller;

import com.liquordb.dto.review.ReviewRequestDto;
import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.service.ReviewService;
import com.liquordb.UserValidator;
import com.liquordb.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<?> create(
            @RequestBody @Valid ReviewRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {

        userValidator.validateCanPost(user);
        ReviewResponseDto response = reviewService.create(user, requestDto);
        return ResponseEntity.ok(response);
    }

    // 리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<?> update(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewRequestDto requestDto,
            @AuthenticationPrincipal User user
    ) {
        userValidator.validateCanPost(user);
        ReviewResponseDto response = reviewService.update(reviewId, user, requestDto);
        return ResponseEntity.ok(response);
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> delete(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User user
    ) {
        userValidator.validateCanPost(user);
        reviewService.delete(reviewId, user);
        return ResponseEntity.noContent().build();
    }
}

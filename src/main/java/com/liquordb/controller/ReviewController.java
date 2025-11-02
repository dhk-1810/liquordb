package com.liquordb.controller;

import com.liquordb.dto.review.ReviewRequestDto;
import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.dto.review.ReviewUpdateRequestDto;
import com.liquordb.service.ReviewService;
import com.liquordb.UserValidator;
import com.liquordb.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private ReviewService reviewService;
    private UserValidator userValidator;

    // 리뷰 등록
    @PostMapping
    public ResponseEntity<ReviewResponseDto> create(
            @RequestBody @Valid ReviewRequestDto requestDto,
            @AuthenticationPrincipal User user,
            @RequestPart(required = false) List<MultipartFile> images
    ) {
        userValidator.validateCanPost(user);
        ReviewResponseDto response = reviewService.create(user, requestDto, images);
        return ResponseEntity.ok(response);
    }

    // 리뷰 수정
    @PatchMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> update(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewUpdateRequestDto request,
            @AuthenticationPrincipal User user,
            @RequestPart(required = false) List<MultipartFile> imagesToAdd
    ) {
        userValidator.validateCanPost(user);
        ReviewResponseDto response = reviewService.update(reviewId, user, request, imagesToAdd);
        return ResponseEntity.ok(response);
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> delete(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User user
    ) {
        userValidator.validateCanPost(user);
        reviewService.deleteByIdAndUser(reviewId, user);
        return ResponseEntity.noContent().build();
    }
}

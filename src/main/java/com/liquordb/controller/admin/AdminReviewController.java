package com.liquordb.controller.admin;

import com.liquordb.dto.PageResponse;
import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.dto.review.ReviewSearchRequest;
import com.liquordb.entity.Review;
import com.liquordb.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;

    // 리뷰 조회 - 작성 유저, 리뷰 상태 필터링 가능
    @GetMapping
    public ResponseEntity<PageResponse<ReviewResponseDto>> findByUserIdAndReviewStatus(
            @ModelAttribute @Valid ReviewSearchRequest request
    ) {
        PageResponse<ReviewResponseDto> reviews = reviewService.getAll(request);
        return ResponseEntity.ok(reviews);
    }

}
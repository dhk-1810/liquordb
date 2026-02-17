package com.liquordb.mapper;

import com.liquordb.dto.report.ReviewReportRequest;
import com.liquordb.dto.report.ReviewReportResponseDto;
import com.liquordb.entity.ReviewReport;
import com.liquordb.entity.Review;
import com.liquordb.entity.User;
import com.liquordb.exception.review.ReviewNotFoundException;
import com.liquordb.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewReportMapper {

    private final ReviewRepository reviewRepository;

    public ReviewReport toEntity(ReviewReportRequest request, User requestUser) {

        Long reviewId = request.reviewId();
        Review review = reviewRepository.findByIdAndStatus(reviewId, Review.ReviewStatus.ACTIVE)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        return ReviewReport.create(review, requestUser.getId(), request.reason());
    }

    public ReviewReportResponseDto toDto(ReviewReport report){
        return new ReviewReportResponseDto(
                report.getReview().getId(), // TODO N+1 ??
                report.getRequestUserId(),
                report.getReason(),
                report.getStatus(),
                report.getCreatedAt(),
                report.getApprovedAt(),
                report.getRejectedAt()
        );
    }
}

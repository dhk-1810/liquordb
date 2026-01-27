package com.liquordb.mapper;

import com.liquordb.dto.report.ReviewReportRequestDto;
import com.liquordb.dto.report.ReviewReportResponseDto;
import com.liquordb.dto.review.ReviewSummaryDto;
import com.liquordb.entity.Comment;
import com.liquordb.entity.ReviewReport;
import com.liquordb.entity.Review;
import com.liquordb.entity.User;
import com.liquordb.exception.comment.CommentNotFoundException;
import com.liquordb.exception.ReviewNotFoundException;
import com.liquordb.repository.CommentRepository;
import com.liquordb.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReviewReportMapper {

    private final ReviewRepository reviewRepository;

    public ReviewReport toEntity(ReviewReportRequestDto request, User requestUser) {

        Long reviewId = request.reviewId();
        Review review = reviewRepository.findByIdAndStatus_Active(reviewId)
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

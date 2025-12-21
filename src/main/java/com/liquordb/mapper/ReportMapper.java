package com.liquordb.mapper;

import com.liquordb.dto.report.ReportRequestDto;
import com.liquordb.dto.report.ReportResponseDto;
import com.liquordb.dto.review.ReviewSummaryDto;
import com.liquordb.entity.Comment;
import com.liquordb.entity.Report;
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
public class ReportMapper {

    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;

    public Report toReviewReportEntity(ReportRequestDto request, User requestUser) {
        Long reviewId = request.targetId();
        Review review = reviewRepository.findByIdAndStatus_Active(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        return Report.createReviewReport(review, requestUser.getId(), request.reason());
    }

    public Report toCommentReportEntity(ReportRequestDto request, User requestUser) {
        Long commentId = request.targetId();
        Comment comment = commentRepository.findByIdAndStatus_Active(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        return Report.createCommentReport(comment, requestUser.getId(), request.reason());
    }

    public ReportResponseDto toDto(Report report){
        return ReportResponseDto.builder()
                .requestUserId(report.getRequestUserId())
                .targetId(report.getReview().getId())
                .targetType(report.getTargetType())
                .reason(report.getReason())
                .status(report.getStatus())
                .createdAt(LocalDateTime.now())
                .build();
    }
    public static ReviewSummaryDto toSummaryDto(Review review) {
        return ReviewSummaryDto.builder()
                .id(review.getId())
                .title(review.getTitle())
                .content(review.getContent())
                .score(review.getRating())
                .liquorName(review.getLiquor().getName()) // Liquor 엔티티에서 이름 가져옴
                .createdDate(review.getCreatedAt())
                .build();
    }
}

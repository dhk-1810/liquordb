package com.liquordb.review.repository;

import com.liquordb.review.entity.ReviewReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, String> {
    boolean existsByReviewIdAndUserId(Long reviewId, Long userId);
}

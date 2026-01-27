package com.liquordb.repository;

import com.liquordb.entity.ReviewReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {

    // 같은 유저가 이미 신고했는지 여부 (중복 신고 방지)
    boolean existsByReviewIdAndUser_Id(Long reviewId, UUID userId);

    // 누적 신고 건수 count
    long countByReview_Id(Long reviewId);

}

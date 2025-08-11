package com.liquordb.review.repository;

import com.liquordb.review.entity.Report;
import com.liquordb.review.entity.ReportTargetType;
import com.liquordb.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByReporterAndTargetIdAndTargetType(User reporter, Long targetId, ReportTargetType targetType);

    long countByTargetIdAndTargetType(Long targetId, ReportTargetType targetType);
}

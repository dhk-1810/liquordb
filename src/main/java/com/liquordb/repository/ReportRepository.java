package com.liquordb.repository;

import com.liquordb.entity.Report;
import com.liquordb.enums.ReportTargetType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    // 같은 유저가 이미 신고했는지 여부 (중복 신고 방지)
    boolean existsByTargetTypeAndTargetIdAndUserId(ReportTargetType targetType, Long targetId, Long userId);

    // 누적 신고 건수 count
    long countByTargetTypeAndTargetId(ReportTargetType targetType, Long targetId);
}

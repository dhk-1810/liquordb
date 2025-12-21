package com.liquordb.dto.report;

import com.liquordb.entity.Report;
import com.liquordb.enums.ReportTargetType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ReportResponseDto (
        Long targetId, // TODO 분리?
        ReportTargetType targetType,
        UUID requestUserId,
        String reason,
        Report.ReportStatus status,
        LocalDateTime createdAt,
        LocalDateTime approvedAt,
        LocalDateTime rejectedAt
){

}

package com.liquordb.dto.report;

import com.liquordb.enums.ReportStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewReportResponseDto (
        Long reviewId,
        UUID requestUserId,
        String reason,
        ReportStatus status,
        LocalDateTime createdAt,
        LocalDateTime approvedAt,
        LocalDateTime rejectedAt
){
}

package com.liquordb.dto.report;

import com.liquordb.enums.ReportStatus;

import java.time.LocalDateTime;

public record CommentReportResponseDto (
        Long id,
        Long commentId,
        String reporterUsername,
        String commentContent,
        String reason,
        ReportStatus status,
        LocalDateTime createdAt,
        LocalDateTime approvedAt,
        LocalDateTime rejectedAt
){

}

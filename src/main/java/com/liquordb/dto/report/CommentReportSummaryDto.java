package com.liquordb.dto.report;

import com.liquordb.enums.ReportStatus;

import java.time.LocalDateTime;

public record CommentReportSummaryDto (
        Long id,
        String commentSnippet,
        String reporterUsername,
        ReportStatus status,
        LocalDateTime createdAt
) {
}

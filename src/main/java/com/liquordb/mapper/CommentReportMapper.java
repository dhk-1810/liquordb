package com.liquordb.mapper;

import com.liquordb.dto.report.CommentReportResponseDto;
import com.liquordb.dto.report.CommentReportSummaryDto;
import com.liquordb.entity.Comment;
import com.liquordb.entity.CommentReport;

import java.util.UUID;

public class CommentReportMapper {

    public static CommentReport toEntity(Comment comment, String reason, UUID reporterId, String reporterUsername) {
        return CommentReport.create(comment, reason, reporterId, reporterUsername);
    }

    public static CommentReportResponseDto toDto(CommentReport report){
        return new CommentReportResponseDto(
                report.getId(),
                report.getComment().getId(),
                report.getReporterUsername(),
                report.getComment().getContent(),
                report.getReason(),
                report.getStatus(),
                report.getCreatedAt(),
                report.getApprovedAt(),
                report.getRejectedAt()
        );
    }

    public static CommentReportSummaryDto toSummaryDto(CommentReport report){
        String content = report.getComment().getContent();
        String snippet = content.length() > 20 ? content.substring(0, 20) + "..." : content;
        return new CommentReportSummaryDto(
                report.getId(),
                snippet,
                report.getReporterUsername(),
                report.getStatus(),
                report.getCreatedAt()
        );
    }
}

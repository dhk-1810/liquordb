package com.liquordb.mapper;

import com.liquordb.dto.report.CommentReportRequestDto;
import com.liquordb.dto.report.CommentReportResponseDto;
import com.liquordb.entity.Comment;
import com.liquordb.entity.CommentReport;
import com.liquordb.entity.ReviewReport;
import com.liquordb.entity.User;
import com.liquordb.exception.comment.CommentNotFoundException;
import com.liquordb.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

public class CommentReportMapper {

    public static CommentReport toEntity(Comment comment, String reason, UUID requestUserId) {
        return CommentReport.create(comment, reason, requestUserId);
    }

    public static CommentReportResponseDto toDto(CommentReport report){
        return new CommentReportResponseDto(
                report.getComment().getId(), // TODO N+1 ??
                report.getRequestUserId(),
                report.getReason(),
                report.getStatus(),
                report.getCreatedAt(),
                report.getApprovedAt(),
                report.getRejectedAt()
        );
    }
}

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

@Component
@RequiredArgsConstructor
public class CommentReportMapper {

    private final CommentRepository commentRepository;

    public CommentReport toEntity(CommentReportRequestDto request, User requestUser) {
        Long commentId = request.commentId();
        Comment comment = commentRepository.findByIdAndStatus_Active(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        return CommentReport.create(comment, requestUser.getId(), request.reason());
    }

    public CommentReportResponseDto toDto(CommentReport report){
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

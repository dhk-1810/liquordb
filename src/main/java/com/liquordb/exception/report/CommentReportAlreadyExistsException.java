package com.liquordb.exception.report;

import com.liquordb.enums.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class CommentReportAlreadyExistsException extends ReportException {

    public CommentReportAlreadyExistsException(Long commentId, UUID userId) {
        super(
                ErrorCode.COMMENT_REPORT_ALREADY_EXISTS,
                "이미 신고한 댓글입니다.",
                Map.of("commentReportId", commentId, "requestUserId", userId)
        );
    }

}

package com.liquordb.exception.report;

import com.liquordb.enums.ErrorCode;

import java.util.Map;

public class CommentReportNotFoundException extends ReportException {

    public CommentReportNotFoundException(Long commentReportId) {
        super(
                ErrorCode.COMMENT_REPORT_NOT_FOUND,
                "댓글 신고 내역을 찾을 수 없습니다.",
                Map.of("commentReportId", commentReportId)
        );
    }

}

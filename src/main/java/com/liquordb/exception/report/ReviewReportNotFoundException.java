package com.liquordb.exception.report;

import com.liquordb.enums.ErrorCode;

import java.util.Map;

public class ReviewReportNotFoundException extends ReportException {

    public ReviewReportNotFoundException(Long reviewReportId) {
        super(
                ErrorCode.REVIEW_REPORT_NOT_FOUND,
                "리뷰 신고 내역을 찾을 수 없습니다.",
                Map.of("reviewReportId", reviewReportId)
        );
    }

}
package com.liquordb.exception.report;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.Map;

public class ReviewReportNotFoundException extends EntityNotFoundException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ReviewReportNotFoundException(Map<String, Object> details) {
        super(ErrorCode.REVIEW_REPORT_NOT_FOUND, "리뷰 신고 내역을 찾을 수 없습니다.", details);
    }
}
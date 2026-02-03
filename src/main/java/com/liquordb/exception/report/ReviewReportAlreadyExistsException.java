package com.liquordb.exception.report;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.AlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.Map;
import java.util.UUID;

public class ReviewReportAlreadyExistsException extends AlreadyExistsException {

    public ReviewReportAlreadyExistsException(Long reviewId, UUID userId) {
        super(
                ErrorCode.REVIEW_REPORT_ALREADY_EXISTS,
                "이미 신고한 리뷰입니다.",
                Map.of("reviewId", reviewId, "userId", userId)
        );
    }

}

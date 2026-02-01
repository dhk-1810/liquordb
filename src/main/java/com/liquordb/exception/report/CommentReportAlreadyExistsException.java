package com.liquordb.exception.report;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.AlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.Map;

public class CommentReportAlreadyExistsException extends AlreadyExistsException {

    public CommentReportAlreadyExistsException(Map<String, Object> details) {
        super(ErrorCode.COMMENT_REPORT_ALREADY_EXISTS, "이미 신고한 댓글입니다.", details);
    }

}

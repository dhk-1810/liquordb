package com.liquordb.exception.review;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.LiquordbException;

import java.util.Map;

public abstract class ReviewException extends LiquordbException {
    public ReviewException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
}

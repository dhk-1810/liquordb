package com.liquordb.exception.file;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.LiquordbException;

import java.util.Map;

public abstract class FIleException extends LiquordbException {
    public FIleException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
}

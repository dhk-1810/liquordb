package com.liquordb.exception;

import com.liquordb.enums.ErrorCode;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Map;

public abstract class LiquordbException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public final LocalDateTime timestamp;
    public final ErrorCode errorCode;
    public final Map<String, Object> details;

    protected LiquordbException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(message);
        this.timestamp = LocalDateTime.now();
        this.errorCode = errorCode;
        this.details = details;
    }
}

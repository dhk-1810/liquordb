package com.liquordb.exception;

import com.liquordb.enums.ErrorCode;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse (
        LocalDateTime timestamp,
        int status,
        ErrorCode code,
        String message,
        Map<String, Object> details
){
    public static ErrorResponse of(HttpStatus status, ErrorCode code, String message, Map<String, Object> details) {
        return new ErrorResponse(LocalDateTime.now(), status.value(), code, message, details);
    }
}
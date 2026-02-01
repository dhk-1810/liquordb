package com.liquordb.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse (
        LocalDateTime timestamp,
        int status,
        String message,
        Map<String, Object> details
){
    public static ErrorResponse of(HttpStatus status, String message, Map<String, Object> details) {
        return new ErrorResponse(LocalDateTime.now(), status.value(), message, details);
    }
}
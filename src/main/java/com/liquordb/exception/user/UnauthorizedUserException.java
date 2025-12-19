package com.liquordb.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedUserException extends RuntimeException {
    public UnauthorizedUserException(UUID userId) {
        super("접근 권한이 없습니다. userId = " + userId);
    }
}

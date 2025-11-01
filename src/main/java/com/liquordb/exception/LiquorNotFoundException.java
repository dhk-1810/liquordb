package com.liquordb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LiquorNotFoundException extends EntityNotFoundException {
    public LiquorNotFoundException(Long id) {
        super("주류를 찾을 수 없습니다. ID=" + id);
    }
}
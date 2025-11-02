package com.liquordb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoticeNotFoundException extends EntityNotFoundException {
    public NoticeNotFoundException(Long id) {
        super("공지를 찾을 수 없습니다. ID=" + id);
    }
}
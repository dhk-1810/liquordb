package com.liquordb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReportNotFoundException extends EntityNotFoundException {
    public ReportNotFoundException(Long id) {
        super("신고 내역을 찾을 수 없습니다. ID=" + id);
    }
}
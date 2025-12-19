package com.liquordb.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class SuspendedUserException extends RuntimeException {
    public SuspendedUserException(LocalDateTime suspendedUntil) {
        super("게시글, 댓글 작성이 제한되었습니다. 해제 일시=" +  suspendedUntil.toString());
    }
}

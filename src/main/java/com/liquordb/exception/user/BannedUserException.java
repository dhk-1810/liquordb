package com.liquordb.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class BannedUserException extends RuntimeException {
    public BannedUserException(String email) {
        super("회원가입이 제한된 이메일입니다. 이메일=" + email);
    }
}

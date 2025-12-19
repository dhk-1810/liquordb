package com.liquordb.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class LoginFailedException extends RuntimeException {
    public LoginFailedException() {
        super("아이디 또는 비밀번호가 틀렸습니다.");
    }
}

package com.liquordb.exception.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class BannedUserException extends RuntimeException {
    public BannedUserException(String email) {
        super("회원가입이 제한된 이메일입니다. 이메일=" + email);
    }
    public BannedUserException(UUID id) {
        super("강제 탈퇴된 사용자입니다. ID=" + id);
    }
}

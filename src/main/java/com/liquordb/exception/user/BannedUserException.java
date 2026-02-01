package com.liquordb.exception.user;

import org.springframework.security.authentication.DisabledException;

public class BannedUserException extends DisabledException {

    public BannedUserException() {
        super("강제 탈퇴 처리되었습니다. 재가입 불가능합니다.");
    }

}
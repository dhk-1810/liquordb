package com.liquordb.exception.auth;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.user.UserException;

import java.util.Map;

public class LoginFailedException extends AuthException {

    public LoginFailedException() {
        super(
                ErrorCode.LOGIN_FAILED,
                "아이디 또는 비밀번호를 다시 확인하세요.",
                Map.of()
        );
    }

}

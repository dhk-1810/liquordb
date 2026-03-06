package com.liquordb.exception.auth;

import com.liquordb.enums.ErrorCode;

import java.util.Map;

public class UnauthenticatedUserException extends AuthException {

    public UnauthenticatedUserException() {
        super(
                ErrorCode.UNAUTHENTICATED_USER,
                "로그인이 필요합니다.",
                Map.of()
        );
    }

}

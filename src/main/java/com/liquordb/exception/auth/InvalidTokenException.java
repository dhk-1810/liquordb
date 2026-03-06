package com.liquordb.exception.auth;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.user.UserException;

import java.util.Map;

public class InvalidTokenException extends AuthException {

    public InvalidTokenException() {
        super(
                ErrorCode.INVALID_TOKEN,
                "토큰이 유효하지 않습니다.",
                Map.of()
        );
    }

}

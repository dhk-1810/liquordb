package com.liquordb.exception.user;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.LiquordbException;

import java.util.Map;

public class UnauthenticatedUserException extends LiquordbException {

    public UnauthenticatedUserException() {
        super(
                ErrorCode.UNAUTHENTICATED_USER,
                "로그인이 필요합니다.",
                null
        );
    }

}

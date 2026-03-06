package com.liquordb.exception.auth;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.LiquordbException;

import java.util.Map;

public abstract class AuthException extends LiquordbException {

    protected AuthException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }

}

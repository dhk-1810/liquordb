package com.liquordb.exception.user;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.LiquordbException;

import java.io.Serial;
import java.util.Map;

public class LoginFailedException extends LiquordbException {

    public LoginFailedException(Map<String, Object> details) {
        super(ErrorCode.LOGIN_FAILED ,"아이디 또는 비밀번호를 다시 확인하세요.", details);
    }

}

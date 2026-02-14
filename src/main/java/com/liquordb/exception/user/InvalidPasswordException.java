package com.liquordb.exception.user;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.LiquordbException;

import java.util.Map;

/**
 * 마이페이지 비밀번호 변경
 */
public class InvalidPasswordException extends UserException {

    public InvalidPasswordException() {
        super(
                ErrorCode.INVALID_PASSWORD,
                "비밀번호가 틀렸습니다.",
                Map.of()
        );
    }

}

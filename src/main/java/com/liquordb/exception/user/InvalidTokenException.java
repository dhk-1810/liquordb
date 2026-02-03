package com.liquordb.exception.user;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.LiquordbException;

import java.util.Map;

public class InvalidTokenException extends LiquordbException {

    public InvalidTokenException() {
        super(
                ErrorCode.INVALID_TOKEN,
                "토큰이 유효하지 않습니다.",
                Map.of()
        );
    }

}

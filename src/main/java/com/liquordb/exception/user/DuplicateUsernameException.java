package com.liquordb.exception.user;

import com.liquordb.enums.ErrorCode;

import java.util.Map;

public class DuplicateUsernameException extends UserException {

    public DuplicateUsernameException(String username) {
        super(
                ErrorCode.DUPLICATE_USERNAME,
                "해당 닉네임을 가진 사용자가 이미 존재합니다.",
                Map.of("username", username)
        );
    }

}

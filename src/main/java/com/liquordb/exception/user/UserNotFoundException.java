package com.liquordb.exception.user;

import com.liquordb.enums.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class UserNotFoundException extends UserException {

    public UserNotFoundException(UUID userId) {
        super(
                ErrorCode.USER_NOT_FOUND,
                "사용자를 찾을 수 없습니다.",
                Map.of("userId", userId)
        );
    }

    public UserNotFoundException(String username) {
        super(
                ErrorCode.USER_NOT_FOUND,
                "사용자를 찾을 수 없습니다.",
                Map.of("username", username)
        );
    }

}
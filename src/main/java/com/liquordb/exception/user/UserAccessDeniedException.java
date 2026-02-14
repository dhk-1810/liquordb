package com.liquordb.exception.user;

import com.liquordb.enums.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class UserAccessDeniedException extends UserException {

    public UserAccessDeniedException(UUID userID) {
        super(
                ErrorCode.USER_ACCESS_DENIED,
                "사용자 정보 접근 권한이 없습니다.",
                Map.of("userId", userID)
        );
    }

}

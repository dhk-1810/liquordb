package com.liquordb.exception.auth;

import com.liquordb.enums.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class SuspendedUserException extends AuthException {

    public SuspendedUserException(UUID userId) {
        super(
                ErrorCode.SUSPENDED_USER,
                "게시글, 댓글 작성이 제한되었습니다.",
                Map.of("userId", userId)
        );
    }

}

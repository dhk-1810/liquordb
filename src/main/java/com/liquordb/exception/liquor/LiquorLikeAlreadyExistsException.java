package com.liquordb.exception.liquor;

import com.liquordb.enums.ErrorCode;

import java.util.Map;

public class LiquorLikeAlreadyExistsException extends LiquorException {

    public LiquorLikeAlreadyExistsException(Long liquorId) {
        super(
                ErrorCode.LIQUOR_LIKE_ALREADY_EXISTS,
                "이미 좋아요 한 주류입니다",
                Map.of("liquorId", liquorId)
        );
    }
}

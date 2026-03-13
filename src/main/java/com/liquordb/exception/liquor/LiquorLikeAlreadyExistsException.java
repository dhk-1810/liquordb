package com.liquordb.exception.liquor;

import com.liquordb.enums.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class LiquorLikeAlreadyExistsException extends LiquorException {

    public LiquorLikeAlreadyExistsException(Long liquorId, UUID userId) {
        super(
                ErrorCode.LIQUOR_LIKE_ALREADY_EXISTS,
                "이미 좋아요 한 주류입니다",
                Map.of(
                        "liquorId", liquorId,
                        "userId", userId
                )
        );
    }
}

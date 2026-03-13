package com.liquordb.exception.liquor;

import com.liquordb.enums.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class LiquorLikeNotFoundException extends LiquorException {

    public LiquorLikeNotFoundException(Long liquorId, UUID userId) {
        super(
                ErrorCode.LIQUOR_LIKE_NOT_FOUND,
                "좋아요 내역을 찾을 수 없습니다.",
                Map.of(
                        "liquorId", liquorId,
                        "userId", userId
                )
        );
    }
}

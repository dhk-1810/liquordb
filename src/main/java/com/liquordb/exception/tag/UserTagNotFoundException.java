package com.liquordb.exception.tag;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.EntityNotFoundException;

import java.util.Map;
import java.util.UUID;

public class UserTagNotFoundException extends EntityNotFoundException {

    public UserTagNotFoundException(UUID userId, Long tagId) {
        super(
                ErrorCode.USER_TAG_NOT_FOUND,
                "태그 추가 내역이 없습니다.",
                Map.of("userId", userId, "tagId", tagId)
        );
    }

}

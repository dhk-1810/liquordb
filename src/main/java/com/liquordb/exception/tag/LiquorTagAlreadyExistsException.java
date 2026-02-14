package com.liquordb.exception.tag;

import com.liquordb.enums.ErrorCode;

import java.util.Map;

public class LiquorTagAlreadyExistsException extends TagException {

    public LiquorTagAlreadyExistsException(Long liquorId, Long tagId) {
        super(
                ErrorCode.LIQUOR_TAG_ALREADY_EXISTS,
                "이미 등록한 태그입니다.",
                Map.of("liquorId", liquorId, "tagId", tagId)
        );
    }

}
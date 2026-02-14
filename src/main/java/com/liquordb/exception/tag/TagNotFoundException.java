package com.liquordb.exception.tag;

import com.liquordb.enums.ErrorCode;

import java.util.Map;

public class TagNotFoundException extends TagException {

    public TagNotFoundException(Long tagId) {
        super(
                ErrorCode.TAG_NOT_FOUND,
                "태그를 찾을 수 없습니다.",
                Map.of("tagId", tagId)
        );
    }

}
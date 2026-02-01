package com.liquordb.exception.tag;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.AlreadyExistsException;

import java.util.Map;

public class LiquorTagAlreadyExistsException extends AlreadyExistsException {

    public LiquorTagAlreadyExistsException(Map<String, Object> details) {
        super(ErrorCode.LIQUOR_TAG_ALREADY_EXISTS, "이미 등록한 태그입니다.", details);
    }

}
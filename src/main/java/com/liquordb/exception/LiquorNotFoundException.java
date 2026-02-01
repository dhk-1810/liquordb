package com.liquordb.exception;

import com.liquordb.enums.ErrorCode;

import java.io.Serial;
import java.util.Map;

public class LiquorNotFoundException extends EntityNotFoundException {

    public LiquorNotFoundException(Map<String, Object> details) {
        super(ErrorCode.LIQUOR_NOT_FOUND, "주류를 찾을 수 없습니다.", details);
    }

}
package com.liquordb.exception.liquor;

import com.liquordb.enums.ErrorCode;

import java.util.Map;

public class LiquorNotFoundException extends LiquorException {

    public LiquorNotFoundException(Long liquorId) {
        super(
                ErrorCode.LIQUOR_NOT_FOUND,
                "주류를 찾을 수 없습니다.",
                Map.of("liquorId", liquorId)
        );
    }

    public LiquorNotFoundException(String liquorName) {
        super(
                ErrorCode.LIQUOR_NOT_FOUND,
                "주류를 찾을 수 없습니다.",
                Map.of("liquorName", liquorName)
        );
    }

}
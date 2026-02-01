package com.liquordb.exception.review;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.EntityNotFoundException;

import java.util.Map;

public class ReviewNotFoundException extends EntityNotFoundException {

    public ReviewNotFoundException(Map<String, Object> details) {
        super(ErrorCode.REVIEW_NOT_FOUND, "리뷰를 찾을 수 없습니다.", details);
    }

}
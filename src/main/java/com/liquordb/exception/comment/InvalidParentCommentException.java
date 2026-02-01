package com.liquordb.exception.comment;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.LiquordbException;

import java.util.Map;

public class InvalidParentCommentException extends LiquordbException {

    public InvalidParentCommentException(Map<String, Object> details) {
        super(ErrorCode.BANNED_USER, "부모 댓글 정보가 잘못되었습니다.", details);
    }

}

package com.liquordb.exception.comment;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

public class CommentNotFoundException extends EntityNotFoundException {

    public CommentNotFoundException(Map<String, Object> details) {
        super(ErrorCode.COMMENT_NOT_FOUND, "댓글을 찾을 수 없습니다.", details);
    }

}

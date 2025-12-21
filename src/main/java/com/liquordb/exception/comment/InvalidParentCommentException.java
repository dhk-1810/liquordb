package com.liquordb.exception.comment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidParentCommentException extends RuntimeException {
    public InvalidParentCommentException(Long parentId) {
        super("부모 댓글 정보가 잘못되었습니다. ID=" + parentId);
    }
}

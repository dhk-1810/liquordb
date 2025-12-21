package com.liquordb.exception.tag;

import com.liquordb.exception.AlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserTagAlreadyExistsException extends AlreadyExistsException {
    public UserTagAlreadyExistsException(Long tagId) {
        super("이미 등록된 태그입니다. ID=" + tagId);
    }
}

package com.liquordb.exception.tag;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.AlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

public class UserTagAlreadyExistsException extends AlreadyExistsException {

    public UserTagAlreadyExistsException(Map<String, Object> details) {
        super(ErrorCode.USER_TAG_ALREADY_EXISTS, "이미 추가한 태그입니다.", details);
    }

}

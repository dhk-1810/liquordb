package com.liquordb.exception.user;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.AlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.Map;

public class EmailAlreadyExistsException extends AlreadyExistsException {

    public EmailAlreadyExistsException(Map<String, Object> details) {
        super(ErrorCode.EMAIL_ALREADY_EXISTS, "해당 이메일로 가입된 사용자가 이미 존재합니다.", details);
    }

}

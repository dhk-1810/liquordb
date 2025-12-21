package com.liquordb.exception.user;

import com.liquordb.exception.AlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyExistsException extends AlreadyExistsException {
    public EmailAlreadyExistsException(String email) {
        super("해당 이메일로 가입된 사용자가 이미 존재합니다. 이메일=" + email);
    }
}

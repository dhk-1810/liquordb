package com.liquordb.exception.user;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.AlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.Map;

public class UsernameAlreadyExistsException extends AlreadyExistsException {

    public UsernameAlreadyExistsException(String username) {
        super(
                ErrorCode.USERNAME_ALREADY_EXISTS,
                "해당 닉네임을 가진 사용자가 이미 존재합니다.",
                Map.of("username", username)
        );
    }
}

package com.liquordb.exception.user;

import com.liquordb.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(UUID id) {
        super("사용자를 찾을 수 없습니다. ID: " + id);
    }

    public UserNotFoundException(String email) {
        super("사용자를 찾을 수 없습니다. 이메일: " + email);
    }

}
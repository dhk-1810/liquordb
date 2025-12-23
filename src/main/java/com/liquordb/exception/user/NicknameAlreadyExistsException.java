package com.liquordb.exception.user;

import com.liquordb.exception.AlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class NicknameAlreadyExistsException extends AlreadyExistsException {
    public NicknameAlreadyExistsException(String nickname) {
        super("해당 닉네임으로 가입된 사용자가 이미 존재합니다. 닉네임=" + nickname);
    }
}

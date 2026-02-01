package com.liquordb.exception.user;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.AlreadyExistsException;

import java.util.Map;

public class DuplicateEmailException extends AlreadyExistsException { // TODO 부모도 수정?

    public DuplicateEmailException(String email) {
        super(
                ErrorCode.DUPLICATE_EMAIL,
                "해당 이메일로 가입된 사용자가 이미 존재합니다.",
                Map.of("email", email)
        );
    }

}

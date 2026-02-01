package com.liquordb.exception.tag;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.AlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;
import java.util.UUID;

public class UserTagAlreadyExistsException extends AlreadyExistsException {

    public UserTagAlreadyExistsException(UUID userId, Long tagId) {
        super(
                ErrorCode.USER_TAG_ALREADY_EXISTS,
                "이미 추가한 태그입니다.",
                Map.of("userId", userId, "tagId", tagId)
        );
    }

}

package com.liquordb.exception;

import com.liquordb.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

public abstract class EntityNotFoundException extends LiquordbException {

    public EntityNotFoundException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }

}

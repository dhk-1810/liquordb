package com.liquordb.exception;

import com.liquordb.enums.ErrorCode;

import java.io.Serial;
import java.util.Map;

public class NoticeNotFoundException extends EntityNotFoundException {

    public NoticeNotFoundException(Map<String, Object> details) {
        super(ErrorCode.NOTICE_NOT_FOUND, "공지를 찾을 수 없습니다.", details);
    }

}
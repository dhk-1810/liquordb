package com.liquordb.exception.notice;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.EntityNotFoundException;

import java.util.Map;

public class NoticeNotFoundException extends EntityNotFoundException {

    public NoticeNotFoundException(Long noticeId) {
        super(
                ErrorCode.NOTICE_NOT_FOUND,
                "공지를 찾을 수 없습니다.",
                Map.of("noticeId", noticeId)
        );
    }

}
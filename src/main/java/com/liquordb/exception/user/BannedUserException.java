package com.liquordb.exception.user;

import com.liquordb.enums.ErrorCode;
import com.liquordb.exception.LiquordbException;

import java.io.Serial;
import java.util.Map;

public class BannedUserException extends LiquordbException {

    public BannedUserException(Map<String, Object> details) {
        super(ErrorCode.BANNED_USER, "신고 누적으로 강제 탈퇴 처리되었습니다.", details);
    }

}
package com.liquordb.exception.user;

import org.springframework.security.authentication.DisabledException;

/**
 * 자발적 회원 탈퇴 후 복구 가능기간 내 다시 회원가입 시도할 때 던짐.
 */
public class WithdrawnUserException extends DisabledException {

    public WithdrawnUserException() {
        super("삭제 요청된 계정입니다. 복구를 원하시면 로그인 해주세요");
    }

}

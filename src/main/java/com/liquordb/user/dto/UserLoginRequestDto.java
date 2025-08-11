package com.liquordb.user.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 요청 DTO입니다. 로그인은 이메일과 비밀번호로 진행됩니다.
 */
@Getter
@Setter
public class UserLoginRequestDto {
    private String email;
    private String password;
}

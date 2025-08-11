package com.liquordb.user.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 회원가입 요청 DTO입니다.
 */
@Getter
@Setter
public class UserRegisterRequestDto {
    private String email;
    private String password;
    private String nickname;
}


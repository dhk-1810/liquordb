package com.liquordb.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 회원가입 요청 DTO입니다.
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequestDto {
    private String email;
    private String password;
    private String nickname;
}


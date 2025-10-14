package com.liquordb.dto.user;

import lombok.*;

/**
 * 회원가입 요청 DTO입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterRequestDto {
    private String email;
    private String password;
    private String nickname;
}


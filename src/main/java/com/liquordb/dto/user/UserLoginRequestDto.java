package com.liquordb.dto.user;

import lombok.*;

/**
 * 로그인 요청 DTO입니다. 로그인은 이메일과 비밀번호로 진행됩니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRequestDto {
    private String email;
    private String password;
}

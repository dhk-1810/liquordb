package com.liquordb.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 로그인 요청 DTO입니다. 로그인은 이메일과 비밀번호로 진행됩니다.
 */
@Builder
public record UserLoginRequestDto (

        @NotBlank(message = "이메일이 공백입니다. 다시 입력해 주세요.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "비밀번호가 공백입니다. 다시 입력해 주세요.")
        String password
){

}

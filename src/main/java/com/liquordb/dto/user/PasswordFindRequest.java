package com.liquordb.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 비밀번호 찾기 요청 DTO
 */
public record PasswordFindRequest(

        @NotBlank(message = "이메일이 공백입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email
) {

}
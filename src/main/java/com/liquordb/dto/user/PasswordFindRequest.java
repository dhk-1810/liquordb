package com.liquordb.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 비밀번호 찾기 요청 DTO
 */
public record PasswordFindRequest(

        @NotBlank(message = "이메일이 공백입니다.")
        @Pattern(
                regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
                message = "이메일 형식이 올바르지 않습니다."
        )
        String email
) {

}
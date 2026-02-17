package com.liquordb.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * 비밀번호 수정 요청 DTO (로그인 상태에서 사용)
 */
@Builder
public record PasswordUpdateRequest(

        @NotBlank(message = "비밀번호가 공백입니다. 다시 입력해 주세요.")
        String currentPassword,

        @NotBlank(message = "비밀번호가 공백입니다. 다시 입력해 주세요.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
                message = "비밀번호는 8~20자이며, 영문 대소문자, 숫자, 특수문자를 모두 포함해야 합니다."
        )
        String newPassword
){

}
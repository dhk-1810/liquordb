package com.liquordb.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * 비밀번호 재설정 시 사용
 */
public record PasswordResetRequest(

        @NotBlank(message = "비밀번호가 공백입니다. 다시 입력해 주세요.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
                message = "비밀번호는 8~20자이며, 영문 대소문자, 숫자, 특수문자를 모두 포함해야 합니다."
        )
        String password,

        @NotNull(message = "토큰 정보는 필수입니다.")
        String token
) {
}

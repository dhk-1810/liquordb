package com.liquordb.dto.user;

import jakarta.validation.constraints.Pattern;

/**
 * 유저 회원정보 수정 DTO
 */
public record UserUpdateRequest(

        @Pattern(
                regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
                message = "이메일 형식이 올바르지 않습니다."
        )
        String email,

        String username,

        Boolean deleteProfileImage
){

}
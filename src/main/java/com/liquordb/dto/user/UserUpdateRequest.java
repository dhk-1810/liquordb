package com.liquordb.dto.user;

import jakarta.validation.constraints.Email;

/**
 * 유저 회원정보 수정 DTO
 */
public record UserUpdateRequest(

        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        String username,

        boolean deleteProfileImage
){

}
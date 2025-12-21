package com.liquordb.dto.user;

import lombok.*;

/**
 * 유저 비밀번호 변경 DTO입니다.
 */
@Builder
public record UserUpdatePasswordDto (
        String currentPassword,
        String newPassword
){

}
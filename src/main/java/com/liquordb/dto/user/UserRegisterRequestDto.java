package com.liquordb.dto.user;

import lombok.*;

/**
 * 회원가입 요청 DTO입니다.
 */
@Builder
public record UserRegisterRequestDto (
        String email,
        String password,
        String nickname
){

}


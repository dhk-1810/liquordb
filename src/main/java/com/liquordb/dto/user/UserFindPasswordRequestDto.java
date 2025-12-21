package com.liquordb.dto.user;

import lombok.*;

/**
 * 비밀번호 찾기 요청 DTO
 */
@Builder
public record UserFindPasswordRequestDto (
        String email
) {

}
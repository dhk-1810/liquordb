package com.liquordb.dto.user;

import com.liquordb.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 로그인 응답 DTO
 */
@Builder
public record UserLoginResponseDto (
        UUID id,
        String email,
        String nickname,
        User.Role role
){

}

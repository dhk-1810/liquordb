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

    public static UserLoginResponseDto from(User user) { // TODO 필요한가?
        return UserLoginResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();
    }
}

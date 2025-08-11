package com.liquordb.user.dto;

import com.liquordb.user.entity.User;
import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 응답 DTO입니다.
 */
@Getter
@Builder
public class UserLoginResponseDto {
    private Long id;
    private String email;
    private String nickname;
    private User.Role role;

    public static UserLoginResponseDto fromEntity(User user) {
        return UserLoginResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();
    }
}

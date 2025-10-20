package com.liquordb.mapper;

import com.liquordb.dto.user.UserResponseDto;
import com.liquordb.entity.User;

public class UserMapper {
    public static UserResponseDto toDto(User user){
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole().name())
                .build();
    }
}
package com.liquordb.mapper;

import com.liquordb.dto.user.UserRegisterRequestDto;
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

    public static User toEntity(UserRegisterRequestDto request, String encodedPassword, User.Role role){
        return User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .role(role)
                .build();
    }
}
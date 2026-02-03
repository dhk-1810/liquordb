package com.liquordb.mapper;

import com.liquordb.dto.user.SignUpRequestDto;
import com.liquordb.dto.user.UserResponseDto;
import com.liquordb.entity.User;
import com.liquordb.enums.Role;

public class UserMapper {

    public static UserResponseDto toDto(User user){
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    public static User toEntity(SignUpRequestDto request, String encodedPassword, Role role){
        return User.builder()
                .email(request.email())
                .password(encodedPassword)
                .username(request.username())
                .role(role)
                .build();
    }
}
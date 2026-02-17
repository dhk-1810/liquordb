package com.liquordb.mapper;

import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.dto.user.SignUpRequest;
import com.liquordb.dto.user.UserMyPageDto;
import com.liquordb.dto.user.UserResponseDto;
import com.liquordb.entity.User;
import com.liquordb.enums.Role;

import java.util.List;

public class UserMapper {

    public static User toEntity(SignUpRequest request, String encodedPassword, Role role){
        return User.builder()
                .email(request.email())
                .password(encodedPassword)
                .username(request.username())
                .role(role)
                .build();
    }

    public static UserResponseDto toDto(User user){
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    public static UserMyPageDto toMyPageDto(
            User user,
            long reviewCount,
            long commentCount,
            long likedLiquorCount,
            long likedReviewCount,
            long likedCommentCount,
            List<TagResponseDto> preferredTags
    ) {
        return new UserMyPageDto(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                reviewCount,
                commentCount,
                likedLiquorCount,
                likedReviewCount,
                likedCommentCount,
                preferredTags
        );
    }
}
package com.liquordb.mapper;

import com.liquordb.dto.auth.SignUpRequest;
import com.liquordb.dto.user.UserMyPageDto;
import com.liquordb.dto.user.UserResponseDto;
import com.liquordb.entity.User;

public class UserMapper {

    public static User toEntity(SignUpRequest request, String encodedPassword){
        return User.create(
                request.email(),
                request.username(),
                encodedPassword,
                null // TODO 소셜로그인
        );
    }

    public static UserResponseDto toDto(User user){
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getStatus(),
                user.getRole()
        );
    }

    public static UserMyPageDto toMyPageDto(
            User user,
            String presignedUrl,
            long reviewCount,
            long commentCount,
            long likedLiquorCount,
            long likedReviewCount,
            long likedCommentCount
    ) {
        return new UserMyPageDto(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                presignedUrl,
                reviewCount,
                commentCount,
                likedLiquorCount,
                likedReviewCount,
                likedCommentCount
        );
    }
}
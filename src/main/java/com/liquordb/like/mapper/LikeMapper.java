package com.liquordb.like.mapper;

import com.liquordb.like.dto.LikeRequestDto;
import com.liquordb.like.dto.LikeResponseDto;
import com.liquordb.like.entity.Like;
import com.liquordb.liquor.entity.Liquor;
import com.liquordb.user.entity.User;

import java.time.LocalDateTime;


public class LikeMapper {
    public static Like toEntity(LikeRequestDto dto, User user, Liquor liquor) {
        return Like.builder()
                .user(user)
                .targetId(dto.getTargetId())
                .targetType(dto.getTargetType())
                .likedAt(LocalDateTime.now())
                .build();
    }

    public static LikeResponseDto toDto(Like like) {
        return LikeResponseDto.builder()
                .id(like.getId())
                .userId(like.getUser().getId())
                .targetId(like.getTargetId())
                .targetType(like.getTargetType())
                .likedAt(like.getLikedAt())
                .build();
    }
}

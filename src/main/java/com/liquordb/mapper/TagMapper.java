package com.liquordb.mapper;

import com.liquordb.dto.tag.TagRequestDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.LiquorTag;
import com.liquordb.entity.Tag;
import com.liquordb.entity.UserTag;

public class TagMapper {
    public static Tag toEntity(TagRequestDto request) {
        return Tag.create(request.name());
    }

    public static TagResponseDto toDto(Tag tag) {
        return TagResponseDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }

    //TODO N+1 문제 해결 필요
    public static TagResponseDto toDto(LiquorTag liquorTag) {
        return TagResponseDto.builder()
                .id(liquorTag.getTag().getId())
                .name(liquorTag.getTag().getName())
                .build();
    }

    public static TagResponseDto toDto(UserTag userTag) {
        return TagResponseDto.builder()
                .id(userTag.getTag().getId())
                .name(userTag.getTag().getName())
                .build();
    }
}
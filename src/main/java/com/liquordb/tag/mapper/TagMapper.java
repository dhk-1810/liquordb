package com.liquordb.tag.mapper;

import com.liquordb.liquor.dto.TagRequestDto;
import com.liquordb.liquor.dto.TagResponseDto;
import com.liquordb.tag.entity.Tag;

public class TagMapper {
    public static Tag toEntity(TagRequestDto dto) {
        return Tag.builder()
                .name(dto.getName())
                .build();
    }

    public static TagResponseDto toDto(Tag tag) {
        return TagResponseDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
}
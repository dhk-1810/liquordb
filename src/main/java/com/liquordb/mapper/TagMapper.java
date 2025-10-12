package com.liquordb.mapper;

import com.liquordb.dto.tag.TagRequestDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.Tag;

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
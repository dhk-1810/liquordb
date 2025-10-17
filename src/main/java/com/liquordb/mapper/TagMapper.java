package com.liquordb.mapper;

import com.liquordb.dto.liquor.LiquorTagResponseDto;
import com.liquordb.dto.tag.TagRequestDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.LiquorTag;
import com.liquordb.entity.Tag;

import java.util.Set;

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

    public static LiquorTagResponseDto toLiquorTagDto(LiquorTag liquorTag) {
        return LiquorTagResponseDto.builder()
                .id(liquorTag.getId())
                .liquorId(liquorTag.getLiquor().getId())
                .tagId(liquorTag.getTag().getId())
                .tagName(liquorTag.getTag().getName())
                .build();
    }
}
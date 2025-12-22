package com.liquordb.mapper;

import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.Liquor;
import com.liquordb.entity.LiquorTag;
import com.liquordb.entity.Tag;

public class LiquorTagMapper {
    public static LiquorTag toEntity(Liquor liquor, Tag tag) {
        return LiquorTag.create(liquor, tag);
    }

    public static TagResponseDto toDto(LiquorTag liquorTag) {
        return TagResponseDto.builder()
                .id(liquorTag.getTag().getId())
                .name(liquorTag.getTag().getName())
                .build();
    }
}
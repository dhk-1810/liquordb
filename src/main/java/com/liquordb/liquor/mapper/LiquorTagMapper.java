package com.liquordb.liquor.mapper;

import com.liquordb.liquor.dto.LiquorTagResponseDto;
import com.liquordb.liquor.entity.Liquor;
import com.liquordb.liquor.entity.LiquorTag;
import com.liquordb.tag.entity.Tag;

public class LiquorTagMapper {
    public static LiquorTag toEntity(Liquor liquor, Tag tag) {
        return LiquorTag.builder()
                .liquor(liquor)
                .tag(tag)
                .build();
    }

    public static LiquorTagResponseDto toDto(LiquorTag liquorTag) {
        return LiquorTagResponseDto.builder()
                .liquorId(liquorTag.getLiquor().getId())
                .tagId(liquorTag.getTag().getId())
                .build();
    }
}
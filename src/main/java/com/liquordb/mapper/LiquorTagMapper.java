package com.liquordb.mapper;

import com.liquordb.dto.liquor.LiquorTagResponseDto;
import com.liquordb.entity.Liquor;
import com.liquordb.entity.LiquorTag;
import com.liquordb.entity.Tag;

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
package com.liquordb.mapper;

import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.Liquor;
import com.liquordb.entity.LiquorTag;
import com.liquordb.entity.Tag;

public class LiquorTagMapper {
    public static LiquorTag toEntity(Liquor liquor, Tag tag) {
        return LiquorTag.create(liquor, tag);
    }

    public static TagResponseDto toTagDto(LiquorTag liquorTag) {
        return new TagResponseDto(liquorTag.getTag().getId(), liquorTag.getTag().getName());
    }
}
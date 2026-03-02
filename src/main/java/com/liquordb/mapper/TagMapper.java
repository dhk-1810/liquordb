package com.liquordb.mapper;

import com.liquordb.dto.tag.TagRequest;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.LiquorTag;
import com.liquordb.entity.Tag;

public class TagMapper {

    public static Tag toEntity(TagRequest request) {
        return Tag.create(request.name());
    }

    public static TagResponseDto toDto(Tag tag) {
        return new TagResponseDto(tag.getId(), tag.getName());
    }

    public static TagResponseDto toDto(LiquorTag liquorTag) {
        return new TagResponseDto(liquorTag.getTag().getId(), liquorTag.getTag().getName());
    }

}
package com.liquordb.mapper;

import com.liquordb.dto.tag.TagRequestDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.Tag;

public class TagMapper {
    public static Tag toEntity(TagRequestDto request) {
        return Tag.create(request.name());
    }

    public static TagResponseDto toDto(Tag tag) {
        return new TagResponseDto(
                tag.getId(),
                tag.getName()
        );
    }

}
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
        return new TagResponseDto(tag.getId(), tag.getName());
    }

    public static TagResponseDto toDto(UserTag userTag) {
        return new TagResponseDto(userTag.getTag().getId(), userTag.getTag().getName()); // FETCH JOIN 사용 전제
    }

    public static TagResponseDto toDto(LiquorTag liquorTag) {
        return new TagResponseDto(liquorTag.getTag().getId(), liquorTag.getTag().getName());
    }

}
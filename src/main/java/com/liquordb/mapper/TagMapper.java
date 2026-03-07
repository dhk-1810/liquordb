package com.liquordb.mapper;

import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.LiquorTag;
import com.liquordb.entity.ReviewTag;
import com.liquordb.entity.Tag;

public class TagMapper {

    public static TagResponseDto toDto(Tag tag) {
        return new TagResponseDto(tag.getId(), tag.getName());
    }

    public static TagResponseDto toDto(LiquorTag liquorTag) {
        return new TagResponseDto(liquorTag.getTag().getId(), liquorTag.getTag().getName()); // TODO N+1
    }

    public static TagResponseDto toDto(ReviewTag reviewTag) {
        return new TagResponseDto(reviewTag.getTag().getId(), reviewTag.getTag().getName()); // TODO N+1
    }
}
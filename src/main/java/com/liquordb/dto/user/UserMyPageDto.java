package com.liquordb.dto.user;

import com.liquordb.dto.tag.TagResponseDto;
import lombok.*;

import java.util.List;
import java.util.UUID;

public record UserMyPageDto(
        UUID userId,
        String email,
        String username,

        long likedLiquorCount,
        long likedReviewCount,
        long likedCommentCount,
        long reviewCount,
        long commentCount,

        List<TagResponseDto> preferredTags
){

}
package com.liquordb.dto.review;

import com.liquordb.dto.tag.TagResponseDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
public record ReviewResponseDto (
        Long id,
        UUID userId,
        Long liquorId,
        int rating,
        String title,
        String content,
        Set<TagResponseDto> tags,
        List<String> imageUrls,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){

}

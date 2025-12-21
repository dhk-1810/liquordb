package com.liquordb.dto.review;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record ReviewResponseDto (

        Long id,
        UUID userId,
        Long liquorId,
        Double rating,
        String title,
        String content,
        List<String> imagePaths,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

){

}

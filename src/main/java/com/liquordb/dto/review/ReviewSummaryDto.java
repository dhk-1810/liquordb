package com.liquordb.dto.review;

import lombok.*;

import java.time.LocalDateTime;

@Builder
public record ReviewSummaryDto (
        Long id,
        String title,
        double score,
        String liquorName,
        LocalDateTime createdAt
) {


}

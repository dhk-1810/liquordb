package com.liquordb.dto.review;

import java.time.LocalDateTime;

@Deprecated(forRemoval = true)
public record ReviewSummaryDto (
        Long id,
        String title,
        double score,
        String liquorName,
        LocalDateTime createdAt
) {


}

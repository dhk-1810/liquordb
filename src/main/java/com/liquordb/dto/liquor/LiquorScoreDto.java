package com.liquordb.dto.liquor;

public record LiquorScoreDto (
        Long liquorId,
        long reviewCount,
        long likeCount,
        long commentCount
) {
}
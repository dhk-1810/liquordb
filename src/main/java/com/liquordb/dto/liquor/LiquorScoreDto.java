package com.liquordb.dto.liquor;

public record LiquorScoreDto (
        Long liquorId,
        long reviewCount,
        long likeCount,
        long commentCount
) {
    public static long calculateTotalScore(LiquorScoreDto score){
        return score.reviewCount() * 10 + score.likeCount() * 5 + score.commentCount() * 2;
    }
}
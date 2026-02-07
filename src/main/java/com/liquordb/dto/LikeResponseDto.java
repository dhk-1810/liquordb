package com.liquordb.dto;

public record LikeResponseDto (
        boolean isLiked,
        long likeCount
) {

}

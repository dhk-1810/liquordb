package com.liquordb.dto.user;

import java.util.UUID;

public record UserMyPageDto(
        UUID userId,
        String email,
        String username,
        String profileImageUrl,
        long likedLiquorCount,
        long likedReviewCount,
        long likedCommentCount,
        long reviewCount,
        long commentCount
){

}
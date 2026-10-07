package com.liquordb.dto.user;

public record UserActivityCountDto(
        long reviewCount,
        long commentCount,
        long likedLiquorCount,
        long likedReviewCount,
        long likedCommentCount
) {
    public UserActivityCountDto(
            Long reviewCount,
            Long commentCount,
            Long likedLiquorCount,
            Long likedReviewCount,
            Long likedCommentCount
    ) {
        this(
                reviewCount != null ? reviewCount : 0L,
                commentCount != null ? commentCount : 0L,
                likedLiquorCount != null ? likedLiquorCount : 0L,
                likedReviewCount != null ? likedReviewCount : 0L,
                likedCommentCount != null ? likedCommentCount : 0L
        );
    }

    public static UserActivityCountDto empty() {
        return new UserActivityCountDto(0L, 0L, 0L, 0L, 0L);
    }
}

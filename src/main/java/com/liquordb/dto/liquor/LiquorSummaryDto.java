package com.liquordb.dto.liquor;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiquorSummaryDto {
    private Long id;
    private String name;
    private String imageUrl;
    private Double averageRating;
    private long reviewCount;
    private long likeCount;
    private boolean likedByMe;
}

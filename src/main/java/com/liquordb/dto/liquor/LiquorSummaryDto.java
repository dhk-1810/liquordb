package com.liquordb.dto.liquor;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiquorSummaryDto {
    private Long id;
    private String name;
//    private LiquorCategory category;         // 대분류: BEER, WINE, WHISKY 등
//    private String subcategoryName;  // 소분류: 바이젠, 버번 등
    private String imageUrl;
    private Double averageRating;
    private long reviewCount;
    private long likeCount;
    private boolean likedByMe;
}

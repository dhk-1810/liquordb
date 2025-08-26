package com.liquordb.liquor.dto;

import com.liquordb.liquor.entity.Liquor;
import com.liquordb.liquor.entity.LiquorCategory;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiquorSummaryDto {
    private Long id;
    private String name;
    private LiquorCategory category;         // 대분류: BEER, WINE, WHISKY 등
    private String subcategoryName;  // 소분류: 바이젠, 버번 등
    private String imageUrl;
    private long reviewCount;
    private long likeCount;

    public static LiquorSummaryDto from(Liquor liquor) {
        return LiquorSummaryDto.builder()
                .id(liquor.getId())
                .name(liquor.getName())
                .imageUrl(liquor.getImageUrl())
                .build();
    }
}

package com.liquordb.liquor.dto;

import com.liquordb.liquor.entity.Liquor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LiquorSummaryDto {
    private Long id;
    private String name;
    private String type;         // 대분류: BEER, WINE, WHISKEY 등
    private String subcategory;  // 소분류: 바이젠, 버번 등
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

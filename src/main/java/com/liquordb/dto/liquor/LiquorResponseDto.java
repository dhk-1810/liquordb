package com.liquordb.dto.liquor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.enums.Country;
import com.liquordb.enums.LiquorCategory;
import lombok.*;

import java.util.Locale;
import java.util.Set;

/**
 * 단건 조회 시 표시할 상세 정보
 */
@Builder
public record LiquorResponseDto (
        Long id,
        String name,
        String nameKo,
        LiquorCategory category,
        Long subcategoryId,
        String subcategoryName,

        @JsonIgnore
        Country country,

        String manufacturer,
        double abv,
        boolean isDiscontinued, // 단종 여부
        String imageUrl,

        double averageRating,
        long reviewCount,

        Set<TagResponseDto> tags,
        long likeCount,
        boolean likedByMe,
        LiquorDetailResponseDto liquorDetail
) {
    public String getCountryName() {
        return country != null ? country.getKoreanName() : "알 수 없음";
    }
}

package com.liquordb.dto.liquor;

import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.enums.Country;
import com.liquordb.enums.LiquorCategory;
import lombok.*;

import java.util.Set;

/**
 * 단건 조회 시 표시할 상세 정보
 */
@Builder
public record LiquorResponseDto (
        Long id,
        String name,
        LiquorCategory category,
        Long subcategoryId,
        Long subcategoryName,
        Country country, // TODO 국가별 필터링 추가?
        String manufacturer,
        double abv,
        boolean isDiscontinued, // 단종 여부
        String imageUrl,

        double averageRating,
        long reviewCount,

        Set<TagResponseDto> tags,
        long likeCount,
        boolean likedByMe
) {

}

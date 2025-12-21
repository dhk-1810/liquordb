package com.liquordb.dto.liquor;

import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.entity.Liquor;
import com.liquordb.entity.LiquorSubcategory;
import lombok.*;

import java.util.List;
import java.util.Set;

/**
 * 주류 응답 DTO
 * 단일 조회 페이지에서 사용
 */

@Builder
public record LiquorResponseDto (
    Long id,
    String name,
    Liquor.LiquorCategory category,
    LiquorSubcategory subcategory,
    String country,
    String manufacturer,
    double abv,
    boolean isDiscontinued, // 단종 여부
    String imageUrl,

    double averageRating,
    int reviewCount,
    List<ReviewResponseDto> reviews,
    Set<LiquorTagResponseDto> tags,

    long likeCount,
    boolean likedByMe
) {

}

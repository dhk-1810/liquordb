package com.liquordb.dto.liquor;

import com.liquordb.entity.Liquor;
import com.liquordb.entity.LiquorSubcategory;
import lombok.*;

/**
 * 주류 생성 요청 DTO
 */
@Builder
public record LiquorRequestDto (
    String name,
    Liquor.LiquorCategory category,
    LiquorSubcategory subcategory,
    String country,
    String manufacturer,
    Double abv,
    Boolean isDiscontinued, // 단종 여부
    String imageUrl
) {

}

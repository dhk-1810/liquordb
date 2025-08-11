package com.liquordb.liquor.dto;

import com.liquordb.liquor.entity.LiquorCategory;
import com.liquordb.liquor.entity.LiquorSubCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 주류 하나에 대한 요청 DTO입니다.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiquorRequestDto {
    private String name;
    private LiquorCategory category;
    private LiquorSubCategory subcategory;
    private String country;
    private String manufacturer;
    private Double abv;
    private Boolean isDiscontinued; // 단종 여부
    private String imageUrl;
}

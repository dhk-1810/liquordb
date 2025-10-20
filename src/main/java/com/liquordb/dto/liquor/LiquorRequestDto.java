package com.liquordb.dto.liquor;

import com.liquordb.entity.Liquor;
import com.liquordb.entity.LiquorSubcategory;
import lombok.*;

/**
 * 주류 하나에 대한 요청 DTO입니다.
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiquorRequestDto {
    private String name;
    private Liquor.LiquorCategory category;
    private LiquorSubcategory subcategory;
    private String country;
    private String manufacturer;
    private Double abv;
    private Boolean isDiscontinued; // 단종 여부
    private String imageUrl;
}

package com.liquordb.liquor.dto;

import com.liquordb.liquor.entity.LiquorCategory;
import com.liquordb.liquor.entity.LiquorSubCategory;
import com.liquordb.review.dto.ReviewResponseDto;
import lombok.*;

import java.util.List;

/**
 * 주류 하나에 대한 응답 DTO입니다.
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiquorResponseDto {
    private Long id;
    private String name;
    private LiquorCategory category;
    private LiquorSubCategory subcategory;
    private String country;
    private String manufacturer;
    private double abv;
    private boolean isDiscontinued; // 단종 여부
    private String imageUrl;

    private double averageRating;
    private int reviewCount;
    private List<ReviewResponseDto> reviews;

    private long likeCount;
    private boolean likedByMe;

    private List<String> tags;
}

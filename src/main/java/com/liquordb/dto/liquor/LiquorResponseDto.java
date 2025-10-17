package com.liquordb.dto.liquor;

import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.entity.LiquorCategory;
import com.liquordb.entity.LiquorSubcategory;
import com.liquordb.entity.LiquorTag;
import com.liquordb.entity.Tag;
import lombok.*;

import java.util.List;
import java.util.Set;

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
    private LiquorSubcategory subcategory;
    private String country;
    private String manufacturer;
    private double abv;
    private boolean isDiscontinued; // 단종 여부
    private String imageUrl;

    private double averageRating;
    private int reviewCount;
    private List<ReviewResponseDto> reviews;
    private Set<LiquorTagResponseDto> tags;

    private long likeCount;
    private boolean likedByMe;
}

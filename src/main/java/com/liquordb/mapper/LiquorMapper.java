package com.liquordb.mapper;

import com.liquordb.dto.liquor.LiquorRequest;
import com.liquordb.dto.liquor.LiquorResponseDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.*;

import java.util.Set;

public class LiquorMapper {

    public static LiquorResponseDto toDto(Liquor liquor, Set<TagResponseDto> tags, boolean likedByMe) {

        return LiquorResponseDto.builder()
                .id(liquor.getId())
                .name(liquor.getName())
                .category(liquor.getCategory())
                .subcategory(liquor.getSubcategory())
                .country(liquor.getCountry())
                .manufacturer(liquor.getManufacturer())
                .abv(liquor.getAbv())
                .isDiscontinued(liquor.isDiscontinued())
                .imageUrl(liquor.getImageUrl())

                .averageRating(liquor.getAverageRating())
                .reviewCount(liquor.getReviewCount())
                .tags(tags)

                .likeCount(liquor.getLikeCount())
                .likedByMe(likedByMe)
                .build();
    }

    public static LiquorSummaryDto toSummaryDto(Liquor liquor, boolean likedByMe, long reviewCount, long likeCount) {
        return LiquorSummaryDto.builder()
                .id(liquor.getId())
                .name(liquor.getName())
                .imageUrl(liquor.getImageUrl())
                .averageRating(liquor.getAverageRating())
                .reviewCount(reviewCount)
                .likeCount(likeCount)
                .likedByMe(likedByMe)
                .build();
    }

    public static Liquor toEntity(LiquorRequest request) {
        return Liquor.create(
                request.name(),
                request.category(),
                request.subcategory(),
                request.country(),
                request.manufacturer(),
                request.abv(),
                request.imageUrl());
    }
}

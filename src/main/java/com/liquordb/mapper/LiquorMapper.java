package com.liquordb.mapper;

import com.liquordb.dto.liquor.LiquorRequest;
import com.liquordb.dto.liquor.LiquorResponseDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.*;

import java.util.Set;

public class LiquorMapper {

    public static LiquorResponseDto toDto(Liquor liquor, String imageUrl, Set<TagResponseDto> tags, boolean likedByMe) {

        return LiquorResponseDto.builder()
                .id(liquor.getId())
                .name(liquor.getName())
                .category(liquor.getCategory())
                .subcategoryId(liquor.getSubcategoryId())
                .country(liquor.getCountry())
                .manufacturer(liquor.getManufacturer())
                .abv(liquor.getAbv())
                .isDiscontinued(liquor.isDiscontinued())
                .imageUrl(imageUrl)

                .averageRating(liquor.getAverageRating())
                .reviewCount(liquor.getReviewCount())
                .tags(tags)

                .likeCount(liquor.getLikeCount())
                .likedByMe(likedByMe)
                .build();
    }

    public static LiquorSummaryDto toSummaryDto(Liquor liquor, String imageUrl, boolean likedByMe) {

        return LiquorSummaryDto.builder()
                .id(liquor.getId())
                .name(liquor.getName())
                .imageUrl(imageUrl) // TODO
                .averageRating(liquor.getAverageRating())
                .reviewCount(liquor.getReviewCount())
                .likeCount(liquor.getLikeCount())
                .likedByMe(likedByMe)
                .build();
    }

    public static Liquor toEntity(LiquorRequest request, String imageKey) {
        return Liquor.create(
                request.name(),
                request.category(),
                request.subcategoryId(),
                request.country(),
                request.manufacturer(),
                request.abv(),
                imageKey
        );
    }
}

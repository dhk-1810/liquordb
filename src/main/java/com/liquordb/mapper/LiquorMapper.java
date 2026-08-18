package com.liquordb.mapper;

import com.liquordb.dto.liquor.LiquorDetailResponseDto;
import com.liquordb.dto.liquor.LiquorRequest;
import com.liquordb.dto.liquor.LiquorResponseDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.*;
import com.liquordb.entity.liquordetail.*;

import java.util.Set;

public class LiquorMapper {

    public static LiquorResponseDto toDto(Liquor liquor, String imageUrl, Set<TagResponseDto> tags, boolean likedByMe, String subcategoryName) {

        return LiquorResponseDto.builder()
                .id(liquor.getId())
                .name(liquor.getName())
                .nameKo(liquor.getNameKo())
                .category(liquor.getCategory())
                .subcategoryId(liquor.getSubcategoryId())
                .subcategoryName(subcategoryName)
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
                .liquorDetail(getLiquorDetailResponse(liquor.getDetail()))
                .build();
    }

    public static LiquorSummaryDto toSummaryDto(Liquor liquor, String imageUrl, boolean likedByMe) {

        return LiquorSummaryDto.builder()
                .id(liquor.getId())
                .name(liquor.getName())
                .nameKo(liquor.getNameKo())
                .imageUrl(imageUrl)
                .averageRating(liquor.getAverageRating())
                .reviewCount(liquor.getReviewCount())
                .likeCount(liquor.getLikeCount())
                .likedByMe(likedByMe)
                .build();
    }

    public static Liquor toEntity(LiquorRequest request, String imageKey) {
        return Liquor.create(
                request.name(),
                request.nameKo(),
                request.category(),
                request.subcategoryId(),
                request.country(),
                request.manufacturer(),
                request.abv(),
                imageKey
        );
    }


    private static LiquorDetailResponseDto getLiquorDetailResponse(LiquorDetail detail) {

        if (detail instanceof BeerDetail beer) {
            return LiquorDetailResponseDto.builder()
                    .type("BEER")
                    .malts(beer.getMalts())
                    .hops(beer.getHops())
                    .ibu(beer.getIbu())
                    .build();
        } else if (detail instanceof CocktailDetail cocktail) {
            return LiquorDetailResponseDto.builder()
                    .type("COCKTAIL")
                    .glass(cocktail.getGlass())
                    .instructions(cocktail.getInstructions())
                    .ingredients(cocktail.getIngredients())
                    .build();
        } else if (detail instanceof WineDetail wine) {
            return LiquorDetailResponseDto.builder()
                    .type("WINE")
                    .grapeVariety(wine.getGrapeVariety())
                    .vintage(wine.getVintage())
                    .region(wine.getRegion())
                    .build();
        } else if (detail instanceof WhiskyDetail whisky) {
            return LiquorDetailResponseDto.builder().
                    type("WHISKY")
                    .caskType(whisky.getCaskType())
                    .age(whisky.getAge())
                    .build();
        }
        return null;
    }
}

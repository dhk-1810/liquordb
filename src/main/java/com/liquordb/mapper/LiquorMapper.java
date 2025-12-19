package com.liquordb.mapper;

import com.liquordb.dto.liquor.LiquorRequestDto;
import com.liquordb.dto.liquor.LiquorResponseDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.dto.liquor.LiquorTagResponseDto;
import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.entity.Liquor;
import com.liquordb.entity.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LiquorMapper {

    public static LiquorResponseDto toDto(Liquor liquor, User user) { // User는 null 허용.

        List<ReviewResponseDto> reviewResponseDtos = liquor.getReviews().stream()
                .map(ReviewMapper::toDto)
                .toList();

        Set<LiquorTagResponseDto> tagDtos = liquor.getLiquorTags().stream()
                .map(TagMapper::toLiquorTagDto)
                .collect(Collectors.toSet());

        boolean likedByMe = false;
        if (user != null) {
            likedByMe = liquor.getLikes().stream()
                    .anyMatch(like -> like.getUser().equals(user));
        }
        // 아래처럼 쓸수도 있음.
//        boolean likedByMe = liquorLikeRepository
//                .existsByUserIdAndLiquorId(user.getId(), liquorId);

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
                .reviewCount(liquor.getReviews().size())
                .reviews(reviewResponseDtos)
                .tags(tagDtos)

                .likeCount(liquor.getLikes().size())
                .likedByMe(likedByMe)
                .build();
    }

    public static LiquorSummaryDto toSummaryDto(Liquor liquor, User user){

        boolean likedByMe = liquor.getLikes().stream()
                .anyMatch(like -> like.getUser().equals(user));

        return LiquorSummaryDto.builder()
                .id(liquor.getId())
                .name(liquor.getName())
                .imageUrl(liquor.getImageUrl())
                .averageRating(liquor.getAverageRating())
                .reviewCount(liquor.getReviews().size())
                .likeCount(liquor.getLikes().size())
                .likedByMe(likedByMe)
                .build();
    }

    public static Liquor toEntity(LiquorRequestDto request) {
        return Liquor.builder()
                .name(request.getName())
                .category(request.getCategory())
                .subcategory(request.getSubcategory())
                .country(request.getCountry())
                .manufacturer(request.getManufacturer())
                .abv(request.getAbv())
                .imageUrl(request.getImageUrl())
                .build();
    }
}

package com.liquordb.mapper;

import com.liquordb.dto.liquor.LiquorRequestDto;
import com.liquordb.dto.liquor.LiquorResponseDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.Liquor;
import com.liquordb.entity.Review;
import com.liquordb.entity.User;
import com.liquordb.repository.LiquorLikeRepository;
import com.liquordb.repository.LiquorRepository;
import com.liquordb.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LiquorMapper {

    private final ReviewRepository reviewRepository;
    private final LiquorLikeRepository liquorLikeRepository;

    public LiquorResponseDto toDto(Liquor liquor, User user) { // User는 null 허용.

        List<ReviewResponseDto> reviewResponseDtos = reviewRepository
                .findAllByLiquor_IdAndStatus(liquor.getId(), Review.ReviewStatus.ACTIVE)
                .stream()
                .map(ReviewMapper::toDto)
                .toList();

        Set<TagResponseDto> tagDtos = liquor.getLiquorTags().stream()
                .map(TagMapper::toDto)
                .collect(Collectors.toSet());

        boolean likedByMe = false;
        if (user != null) {
            likedByMe = liquorLikeRepository
                   .existsByUserIdAndLiquorId(user.getId(), liquorId);
        }

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

    public LiquorSummaryDto toSummaryDto(Liquor liquor, User user){

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

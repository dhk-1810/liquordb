package com.liquordb.mapper;

import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.Liquor;
import com.liquordb.dto.review.ReviewRequest;
import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.entity.Review;
import com.liquordb.entity.User;
import com.liquordb.entity.reviewdetail.*;

import com.liquordb.dto.review.ReviewDetailResponseDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ReviewMapper {

    public static Review toEntity(ReviewRequest request, Liquor liquor, User user) {

        Review review = Review.create(request, liquor, user);
        ReviewDetail detail = ReviewDetailMapper.toEntity(request.reviewDetailRequest(), review);
        review.addDetail(detail);
        return review;
    }

    public static ReviewResponseDto toDto(Review review, Set<TagResponseDto> tags, List<String> imageUrls, String userProfileImageUrl, boolean likedByMe) {

        User user = review.getUser();
        UUID userId = user != null ? user.getId() : null;
        String username = user != null ? user.getUsername() : "탈퇴한 사용자";
        String profileUrl = user != null ? userProfileImageUrl : null;

        return ReviewResponseDto.builder()
                .id(review.getId())
                .userId(userId)
                .username(username)
                .userProfileImageUrl(profileUrl)
                .likeCount(review.getLikeCount())
                .commentCount(review.getCommentCount())
                .liquorId(review.getLiquor().getId())
                .content(review.getContent())
                .rating(review.getRating())
                .title(review.getTitle())
                .tags(tags)
                .imageUrls(imageUrls)
                .likedByMe(likedByMe)
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .reviewDetail(getReviewDetailResponse(review.getDetail()))
                .build();
    }

    private static ReviewDetailResponseDto getReviewDetailResponse(ReviewDetail detail) {
        if (detail == null) {
            return null;
        }
        ReviewDetailResponseDto.ReviewDetailResponseDtoBuilder builder = ReviewDetailResponseDto.builder();
        if (detail instanceof BeerReviewDetail beer) {
            builder.type("BEER")
                    .aroma(beer.getAroma())
                    .taste(beer.getTaste())
                    .headRetention(beer.getHeadRetention())
                    .look(beer.getLook());
        } else if (detail instanceof WineReviewDetail wine) {
            builder.type("WINE")
                    .sweetness(wine.getSweetness())
                    .acidity(wine.getAcidity())
                    .body(wine.getBody())
                    .tannin(wine.getTannin());
        } else if (detail instanceof WhiskyReviewDetail whisky) {
            builder.type("WHISKY")
                    .aroma(whisky.getAroma())
                    .taste(whisky.getTaste())
                    .finish(whisky.getFinish())
                    .body(whisky.getBody());
        }
        return builder.build();
    }

}

package com.liquordb.mapper;

import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.Liquor;
import com.liquordb.dto.review.ReviewRequest;
import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.entity.Review;
import com.liquordb.entity.User;
import com.liquordb.entity.reviewdetail.ReviewDetail;

import java.util.List;
import java.util.Set;

public class ReviewMapper {

    public static Review toEntity(ReviewRequest request, Liquor liquor, User user) {

        Review review = Review.create(request, liquor, user);
        ReviewDetail detail = ReviewDetailMapper.toEntity(request.reviewDetailRequest(), review);
        review.addDetail(detail);
        return review;
    }

    public static ReviewResponseDto toDto(Review review, Set<TagResponseDto> tags, List<String> imageUrls, String userProfileImageUrl) {

        return ReviewResponseDto.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .username(review.getUser().getUsername())
                .userProfileImageUrl(userProfileImageUrl)
                .likeCount(review.getLikeCount())
                .commentCount(review.getCommentCount())
                .liquorId(review.getLiquor().getId())
                .content(review.getContent())
                .rating(review.getRating())
                .title(review.getTitle())
                .tags(tags)
                .imageUrls(imageUrls)
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

}

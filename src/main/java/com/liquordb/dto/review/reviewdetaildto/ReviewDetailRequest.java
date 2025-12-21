package com.liquordb.dto.review.reviewdetaildto;

public sealed interface ReviewDetailRequest
        permits BeerReviewRequestDto, WhiskyReviewRequestDto, WineReviewRequestDto {
}
package com.liquordb.mapper;

import com.liquordb.dto.review.reviewdetaildto.BeerReviewRequestDto;
import com.liquordb.dto.review.reviewdetaildto.ReviewDetailRequest;
import com.liquordb.dto.review.reviewdetaildto.WhiskyReviewRequestDto;
import com.liquordb.dto.review.reviewdetaildto.WineReviewRequestDto;
import com.liquordb.entity.Review;
import com.liquordb.entity.reviewdetail.BeerReviewDetail;
import com.liquordb.entity.reviewdetail.ReviewDetail;
import com.liquordb.entity.reviewdetail.WhiskyReviewDetail;
import com.liquordb.entity.reviewdetail.WineReviewDetail;

public class ReviewDetailMapper {

    public static ReviewDetail toEntity(ReviewDetailRequest request, Review review) {
        if (request instanceof BeerReviewRequestDto beer) {
            return BeerReviewDetail.builder()
                    .review(review)
                    .aroma(beer.aroma())
                    .taste(beer.taste())
                    .headRetention(beer.headRetention())
                    .look(beer.look())
                    .build();
        }

        if (request instanceof WhiskyReviewRequestDto whisky) {
            return WhiskyReviewDetail.builder()
                    .review(review)
                    .aroma(whisky.aroma())
                    .taste(whisky.taste())
                    .finish(whisky.finish())
                    .body(whisky.body())
                    .build();
        }

        if (request instanceof WineReviewRequestDto wine) {
            return WineReviewDetail.builder()
                    .review(review)
                    .sweetness(wine.sweetness())
                    .acidity(wine.acidity())
                    .body(wine.body())
                    .tannin(wine.tannin())
                    .build();
        }
        throw new IllegalArgumentException("지원하지 않는 리뷰 상세 타입입니다: " + request.getClass().getSimpleName());
    }
}

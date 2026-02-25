package com.liquordb.mapper;

import com.liquordb.dto.review.reviewdetaildto.BeerReviewRequest;
import com.liquordb.dto.review.reviewdetaildto.ReviewDetailRequest;
import com.liquordb.dto.review.reviewdetaildto.WhiskyReviewRequest;
import com.liquordb.dto.review.reviewdetaildto.WineReviewRequest;
import com.liquordb.entity.Review;
import com.liquordb.entity.reviewdetail.BeerReviewDetail;
import com.liquordb.entity.reviewdetail.ReviewDetail;
import com.liquordb.entity.reviewdetail.WhiskyReviewDetail;
import com.liquordb.entity.reviewdetail.WineReviewDetail;

public class ReviewDetailMapper {

    public static ReviewDetail toEntity(ReviewDetailRequest request, Review review) {
        if (request instanceof BeerReviewRequest beer) {
            return new BeerReviewDetail(
                    beer.aroma(),
                    beer.taste(),
                    beer.headRetention(),
                    beer.look(),
                    review
            );
        }

        if (request instanceof WhiskyReviewRequest whisky) {
            return new WhiskyReviewDetail(
                    whisky.aroma(),
                    whisky.taste(),
                    whisky.finish(),
                    whisky.body(),
                    review
            );
        }

        if (request instanceof WineReviewRequest wine) {
            return new WineReviewDetail(
                    wine.sweetness(),
                    wine.acidity(),
                    wine.body(),
                    wine.tannin(),
                    review
            );
        }

        throw new IllegalArgumentException("지원하지 않는 리뷰 상세 타입입니다: " + request.getClass().getSimpleName());
    }
}
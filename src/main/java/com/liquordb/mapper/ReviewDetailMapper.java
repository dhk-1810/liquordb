package com.liquordb.mapper;

import com.liquordb.dto.review.ReviewRequestDto;
import com.liquordb.dto.review.reviewdetaildto.BeerReviewDetailDto;
import com.liquordb.dto.review.reviewdetaildto.WhiskyReviewDetailDto;
import com.liquordb.dto.review.reviewdetaildto.WineReviewDetailDto;
import com.liquordb.entity.LiquorCategory;
import com.liquordb.entity.Review;
import com.liquordb.entity.reviewdetail.BeerReviewDetail;
import com.liquordb.entity.reviewdetail.ReviewDetail;
import com.liquordb.entity.reviewdetail.WhiskyReviewDetail;
import com.liquordb.entity.reviewdetail.WineReviewDetail;

public class ReviewDetailMapper {

    public static ReviewDetail toEntity(LiquorCategory category, ReviewRequestDto dto, Review review) {
        return switch (category) {
            case BEER -> toBeerDetail(dto.getBeerDetail(), review);
            case WHISKY -> toWhiskyDetail(dto.getWhiskyDetail(), review);
            case WINE -> toWineDetail(dto.getWineDetail(), review);
            default -> null;
        };
    }

    public static BeerReviewDetail toBeerDetail(BeerReviewDetailDto dto, Review review) {
        return BeerReviewDetail.builder()
                .review(review)
                .aroma(dto.getAroma())
                .taste(dto.getTaste())
                .headRetention(dto.getHeadRetention())
                .look(dto.getLook())
                .build();
    }

    public static WhiskyReviewDetail toWhiskyDetail(WhiskyReviewDetailDto dto, Review review) {
        return WhiskyReviewDetail.builder()
                .review(review)
                .aroma(dto.getAroma())
                .taste(dto.getTaste())
                .finish(dto.getFinish())
                .body(dto.getBody())
                .build();
    }

    public static WineReviewDetail toWineDetail(WineReviewDetailDto dto, Review review) {
        return WineReviewDetail.builder()
                .review(review)
                .sweetness(dto.getSweetness())
                .acidity(dto.getAcidity())
                .body(dto.getBody())
                .tannin(dto.getTannin())
                .build();
    }
}

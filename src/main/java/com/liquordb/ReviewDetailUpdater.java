package com.liquordb;

import com.liquordb.dto.review.reviewdetaildto.BeerReviewRequestDto;
import com.liquordb.dto.review.reviewdetaildto.ReviewDetailRequest;
import com.liquordb.dto.review.reviewdetaildto.WhiskyReviewRequestDto;
import com.liquordb.dto.review.reviewdetaildto.WineReviewRequestDto;
import com.liquordb.entity.reviewdetail.BeerReviewDetail;
import com.liquordb.entity.reviewdetail.ReviewDetail;
import com.liquordb.entity.reviewdetail.WhiskyReviewDetail;
import com.liquordb.entity.reviewdetail.WineReviewDetail;
import org.springframework.stereotype.Component;

@Component
public class ReviewDetailUpdater {

    public void updateDetail(ReviewDetail detail, ReviewDetailRequest request) {

        if (detail instanceof BeerReviewDetail beerDetail
                && request instanceof BeerReviewRequestDto beerRequest
        ) {
            updateBeerDetail(beerDetail, beerRequest);
        } else if (detail instanceof WineReviewDetail wineDetail
                && request instanceof WineReviewRequestDto wineRequest
        ) {
            updateWineDetail(wineDetail, wineRequest);
        } else if (detail instanceof WhiskyReviewDetail whiskyDetail
                && request instanceof WhiskyReviewRequestDto whiskyRequest
        ) {
            updateWhiskyDetail(whiskyDetail, whiskyRequest);
        } else {
            throw new IllegalArgumentException("Invalid detail request");
        }
    }

    private void updateBeerDetail(BeerReviewDetail entity, BeerReviewRequestDto request) {
        entity.update(request.aroma(), request.taste(), request.headRetention(), request.look());
    }

    private void updateWineDetail(WineReviewDetail entity, WineReviewRequestDto request) {
        entity.update(request.sweetness(), request.acidity(), request.body(), request.tannin());
    }

    private void updateWhiskyDetail(WhiskyReviewDetail entity, WhiskyReviewRequestDto request) {
        entity.update(request.aroma(), request.taste(), request.finish(), request.body());
    }
}

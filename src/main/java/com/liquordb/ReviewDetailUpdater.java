package com.liquordb;

import com.liquordb.dto.review.ReviewRequestDto;
import com.liquordb.entity.reviewdetail.BeerReviewDetail;
import com.liquordb.entity.reviewdetail.ReviewDetail;
import com.liquordb.entity.reviewdetail.WhiskyReviewDetail;
import com.liquordb.entity.reviewdetail.WineReviewDetail;
import org.springframework.stereotype.Component;

@Component
public class ReviewDetailUpdater {

    public void updateDetail(ReviewDetail detail, ReviewRequestDto dto) {
        if (detail instanceof BeerReviewDetail beerDetail && dto.getBeerDetail() != null) {
            updateBeerDetail(beerDetail, dto.getBeerDetail());
        } else if (detail instanceof WineReviewDetail wineDetail && dto.getWineDetail() != null) {
            updateWineDetail(wineDetail, dto.getWineDetail());
        } else if (detail instanceof WhiskyReviewDetail whiskyDetail && dto.getWhiskyDetail() != null) {
            updateWhiskyDetail(whiskyDetail, dto.getWhiskyDetail());
        }
    }

    private void updateBeerDetail(BeerReviewDetail entity, BeerReviewDetailDto dto) {
        entity.setAroma(dto.getAroma());
        entity.setTaste(dto.getTaste());
        entity.setHeadRetention(dto.getHeadRetention());
        entity.setLook(dto.getLook());
    }

    private void updateWineDetail(WineReviewDetail entity, WineReviewDetailDto dto) {
        entity.setBody(dto.getBody());
        entity.setSweetness(dto.getSweetness());
        entity.setAcidity(dto.getAcidity());
        entity.setTannin(dto.getTannin());
    }

    private void updateWhiskyDetail(WhiskyReviewDetail entity, WhiskyReviewDetailDto dto) {
        entity.setAroma(dto.getAroma());
        entity.setTaste(dto.getTaste());
        entity.setFinish(dto.getFinish());
        entity.setBody(dto.getBody());
    }
}

package com.liquordb.repository.review;

import com.liquordb.entity.Review;
import com.liquordb.repository.review.condition.ReviewListGetCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CustomReviewRepository {

    // 리뷰 목록 조회
    Slice<Review> findByLiquorId(ReviewListGetCondition condition);

    Slice<Review> findByUserId(ReviewListGetCondition condition);

    // [관리자용] 리뷰 전체 조회 - 유저별로 필터링(선택), 상태별로 필터링(선택)
    Page<Review> findAll(String username, Review.ReviewStatus status, Pageable pageable);
}

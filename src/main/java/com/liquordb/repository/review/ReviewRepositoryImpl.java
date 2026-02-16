package com.liquordb.repository.review;

import com.liquordb.entity.QReview;
import com.liquordb.entity.Review;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class ReviewRepositoryImpl implements CustomReviewRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QReview review = QReview.review;

    @Override
    public Slice<Review> findByLiquorId(Long liquorId, Review.ReviewStatus status, Pageable pageable) {


        return null;
    }

    @Override
    public Slice<Review> findByUserId(UUID userId, Review.ReviewStatus status, Pageable pageable) {
        return null;
    }

    @Override
    public Page<Review> findAll(String username, Review.ReviewStatus status, Pageable pageable) {
        return null;
    }
}

package com.liquordb.review.repository;

import com.liquordb.review.entity.ReviewDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewDetailRepository extends JpaRepository<ReviewDetail, String> {
}

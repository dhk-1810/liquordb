package com.liquordb.repository;

import com.liquordb.entity.reviewdetail.ReviewDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewDetailRepository extends JpaRepository<ReviewDetail, Long> {
}

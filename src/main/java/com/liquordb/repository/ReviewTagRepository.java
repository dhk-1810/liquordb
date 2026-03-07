package com.liquordb.repository;

import com.liquordb.entity.ReviewTag;
import com.liquordb.entity.id.ReviewTagId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ReviewTagRepository extends JpaRepository<ReviewTag, ReviewTagId> {
    Set<ReviewTag> findByReview_Id(Long reviewId);
}

package com.liquordb.review.repository;

import com.liquordb.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 리뷰 평균 평점
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.liquor.id = :liquorId")
    Double getAverageRatingByLiquorId(@Param("liquorId") Long liquorId);

    // 특정 주류에 달린 리뷰 개수
    int countByLiquorId(Long liquorId);

    // 특정 주류에 달린 리뷰 목록
    List<Review> findAllByLiquorId(Long liquorId);

    // 특정 유저가 남긴 리뷰 개수
    Long countByUserId(Long userId);

    // 특정 유저가 남긴 리뷰 목록
    List<Review> findByUserId(Long userId);
}

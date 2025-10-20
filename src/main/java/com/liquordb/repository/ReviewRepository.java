package com.liquordb.repository;

import com.liquordb.entity.Review;
import com.liquordb.entity.User;
import com.liquordb.entity.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 리뷰 평균 평점
    // TODO 생성, 수정, 삭제시에 알아서 변경되게 할까싶음
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.liquor.id = :liquorId")
    Double getAverageRatingByLiquorId(@Param("liquorId") Long liquorId);

    // 리뷰 단건 조회
    Optional<Review> findByIdAndIsHiddenFalse(Long id);

    // 특정 주류, 유저에 따른 리뷰 목록
    Page<Review> findAllByLiquorIdAndIsHiddenFalse(Pageable pageable, Long liquorId);
    Page<Review> findAllByUserIdAndIsHiddenFalse(Pageable pageable, UUID userId);

    // 좋아요 누른 리뷰 개수
    long countByUserAndIsHiddenFalse(User user);


    // 관리자용 - 유저ID나 상태로 리뷰 목록 조회
    @Query("""
    SELECT r FROM Review r
    WHERE (:userId IS NULL OR r.user.id = :userId)
    AND (:status IS NULL OR r.status = :status)
    """)
    Page<Review> findAllByOptionalFilters(
            @Param("userId") UUID userId,
            @Param("status") Review.ReviewStatus status,
            Pageable pageable
    );

}

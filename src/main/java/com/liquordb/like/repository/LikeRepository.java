package com.liquordb.like.repository;

import com.liquordb.like.entity.Like;
import com.liquordb.like.entity.LikeTargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // 총 좋아요 개수 (target type별) - 주류, 댓글, 리뷰 조회시 사용
    long countByTargetIdAndTargetType(Long targetId, LikeTargetType targetType);

    // 유저가 누른 좋아요 개수 (target type별) - 마이페이지에서 사용
    long countByUserIdAndTargetType(Long userId, LikeTargetType targetType);



    // 유저가 좋이요 누른 객체 목록 (target type별) - 마이페이지에서 사용
    List<Like> findByUserIdAndTargetType(Long userId, LikeTargetType targetType);

    // 좋아요 누른 주류 ID 목록 (유저별)
    @Query("SELECT l.targetId FROM Like l WHERE l.user.id = :userId AND l.targetType = 'LIQUOR'")
    List<Long> findLiquorIdsByUserId(@Param("userId") Long userId);



    // 유저가 특정 객체에 좋아요 눌렀는지 확인 (눌렀는지 여부만)
    boolean existsByUserIdAndTargetIdAndTargetType(Long userId, Long targetId, LikeTargetType targetType);

    // 유저가 특정 객체에 좋아요 눌렀는지 확인 (Like 객체 전체 반환)
    Optional<Like> findByUserIdAndTargetIdAndTargetType(Long userId, Long targetId, LikeTargetType targetType);


}
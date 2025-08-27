package com.liquordb.like.repository;

import com.liquordb.like.entity.LiquorLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LiquorLikeRepository extends JpaRepository<LiquorLike, Long> {
    // 총 좋아요 개수 (target type별) - 주류, 댓글, 리뷰 조회시 사용
    long countByLiquorId(Long liquorId);

    // 유저가 누른 좋아요 개수 - 마이페이지에서 사용
    long countByUserId(Long userId);

    // 유저가 좋이요 누른 객체 목록 - 마이페이지에서 사용
    List<LiquorLike> findByUserId(Long userId);

    // 유저가 특정 객체에 좋아요 눌렀는지 확인 (눌렀는지 여부만)
    boolean existsByUserIdAndLiquorId(Long userId, Long liquorId);

    // 유저가 특정 객체에 좋아요 눌렀는지 확인 (Like 객체 전체 반환)
    Optional<LiquorLike> findByUserIdAndLiquorId(Long userId, Long liquorId);
}

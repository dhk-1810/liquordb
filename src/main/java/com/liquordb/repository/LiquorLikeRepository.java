package com.liquordb.repository;

import com.liquordb.entity.Liquor;
import com.liquordb.entity.LiquorLike;
import com.liquordb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LiquorLikeRepository extends JpaRepository<LiquorLike, Long> {

    // 총 좋아요 개수 (target type별) - 주류, 댓글, 리뷰 조회시 사용
    long countByLiquorIdAndLikedTrue(Long liquorId);

    // 유저가 누른 좋아요 개수 - 마이페이지에서 사용
    long countByUserAndLiquorIsHiddenFalse(User user);

    // 유저가 좋이요 누른 객체 목록 - 마이페이지에서 사용
    List<LiquorLike> findByUserIdAndLiquorIsHiddenFalse(UUID userId);

//    // 유저가 특정 객체에 좋아요 눌렀는지 확인 (눌렀는지 여부만)
//    boolean existsByUserIdAndLiquorId(UUID userId, Long liquorId);

    // 유저가 특정 객체에 좋아요 눌렀는지 확인 (Like 객체 전체 반환)
    Optional<LiquorLike> findByUserIdAndLiquorId(UUID userId, Long liquorId);
}

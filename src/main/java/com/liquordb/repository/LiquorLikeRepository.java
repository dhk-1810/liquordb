package com.liquordb.repository;

import com.liquordb.entity.LiquorLike;
import com.liquordb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface LiquorLikeRepository extends JpaRepository<LiquorLike, Long> {

    // 총 좋아요 개수 (target type별) - 주류, 댓글, 리뷰 조회시 사용
    long countByLiquorIdAndLikedTrue(Long liquorId);

    // 유저가 누른 좋아요 개수 - 마이페이지에서 사용
    long countByUser_IdAndLiquorIsDeletedFalse(UUID userId);

    List<LiquorLike> findAllByLiquor_IdAndLiquorIsDeletedFalse(Long liquorId);

    // 유저가 좋이요 누른 객체 목록 - 마이페이지에서 사용
    List<LiquorLike> findByUser_IdAndLiquor_IsDeletedFalse(UUID userId);

    // 유저가 특정 객체에 좋아요 눌렀는지 확인 (눌렀는지 여부만)
    boolean existsByLiquor_IdAndUser_Id(Long liquorId, UUID userId);

    @Query("SELECT ll.liquor.id FROM LiquorLike ll WHERE ll.user.id = :userId AND ll.liquor.id IN :liquorIds")
    Set<Long> findLikedLiquorIdsByUserIdAndLiquorIds(@Param("userId") UUID userId,
                                                     @Param("liquorIds") List<Long> liquorIds);

    // 유저가 특정 객체에 좋아요 눌렀는지 확인 (Like 객체 전체 반환)
    Optional<LiquorLike> findByLiquor_IdAndUser_Id(Long liquorId, UUID userId);

    UUID user(User user);
}

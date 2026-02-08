package com.liquordb.repository;

import com.liquordb.entity.LiquorLike;
import com.liquordb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface LiquorLikeRepository extends JpaRepository<LiquorLike, Long> {

    long countByLiquor_Id(Long liquorId);
    long countByUser_IdAndLiquorIsDeletedFalse(UUID userId);

    boolean existsByLiquor_IdAndUser_Id(Long liquorId, UUID userId);
    Optional<LiquorLike> findByLiquor_IdAndUser_Id(Long liquorId, UUID userId);

    List<LiquorLike> findByUser_IdAndLiquor_IsDeletedFalse(UUID userId);

    @Query("SELECT ll.liquor.id FROM LiquorLike ll WHERE ll.user.id = :userId AND ll.liquor.id IN :liquorIds")
    Set<Long> findLikedLiquorIdsByUserIdAndLiquorIds(@Param("userId") UUID userId,
                                                     @Param("liquorIds") List<Long> liquorIds);

}

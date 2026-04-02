package com.liquordb.repository.liquor;

import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.entity.Liquor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LiquorRepository extends JpaRepository<Liquor, Long>, CustomLiquorRepository {

    // 주류 단건 조회
    Optional<Liquor> findByIdAndIsDeleted(Long id, boolean isDeleted);

    // 주류 단건 + 태그 조회
    @Query("SELECT l FROM Liquor l " +
            "LEFT JOIN FETCH l.liquorTags lt " +
            "LEFT JOIN FETCH lt.tag " +
            "WHERE l.id = :id AND l.isDeleted = :isDeleted")
    Optional<Liquor> findByIdAndIsDeletedWithTags(
            @Param("id") Long id,
            @Param("isDeleted" ) boolean isDeleted
    );

    // 좋아요 수 변경
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Liquor l SET l.likeCount = l.likeCount + :delta WHERE l.id = :id")
    void updateLikeCount(@Param("id") Long id, @Param("delta") int delta);

    // 인기 주류 조회 시 사용
    List<Liquor> findByIdIn(List<Long> ids);

    // 최신 주류 조회 (Fallback)
    @Query("SELECT l FROM Liquor l ORDER BY l.id DESC")
    List<Liquor> findTopLatestLiquors(Pageable pageable);
}

package com.liquordb.repository;

import com.liquordb.entity.Liquor;
import com.liquordb.entity.LiquorSubcategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LiquorRepository extends JpaRepository<Liquor, Long> {

    // 삭제되지 않은 주류 단건 조회
    Optional<Liquor> findByIdAndIsDeleted(Long id, boolean deleted);

    // 전체 조회
    Page<Liquor> findAllByIsDeleted(Pageable pageable, boolean deleted);

    // 검색
    Page<Liquor> findByNameContainingAndIsDeleted(Pageable pageable, String keyword, boolean deleted);

    // 대분류로 필터링
    Page<Liquor> findByCategoryAndIsDeleted(Pageable pageable, Liquor.LiquorCategory liquorCategory, boolean deleted);

    // 소분류로 필터링
    Page<Liquor> findBySubcategoryAndIsDeleted(Pageable pageable, LiquorSubcategory liquorSubcategory, boolean deleted);

    // 좋아요 수 변경
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Liquor l SET l.likeCount = l.likeCount + :delta WHERE l.id = :id")
    void updateLikeCount(@Param("id") Long id, @Param("delta") int delta);
}

package com.liquordb.liquor.repository;

import com.liquordb.liquor.dto.LiquorSummaryDto;
import com.liquordb.liquor.entity.Liquor;
import com.liquordb.liquor.entity.LiquorSubcategory;
import com.liquordb.liquor.entity.LiquorCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LiquorRepository extends JpaRepository<Liquor, Long> {

    // 전체 조회
    @Query("""
        SELECT new com.liquordb.liquor.dto.LiquorSummaryDto(
            l.id,
            l.name,
            l.category,
            sc.name,
            l.imageUrl,
            COUNT(DISTINCT r.id),
            COUNT(DISTINCT ll.id)
        )
        FROM Liquor l
        JOIN l.subcategory sc
        LEFT JOIN l.reviews r
        LEFT JOIN LiquorLike ll ON ll.liquor = l
        GROUP BY l.id, l.name, l.category, sc.name, l.imageUrl
    """)
    List<LiquorSummaryDto> findAllWithCategoryAndCounts();

    // 검색
    @Query("""
        SELECT new com.liquordb.liquor.dto.LiquorSummaryDto(
            l.id,
            l.name,
            l.category,
            sc.name,
            l.imageUrl,
            COUNT(DISTINCT r.id),
            COUNT(DISTINCT ll.id)
        )
        FROM Liquor l
        JOIN l.subcategory sc
        LEFT JOIN l.reviews r
        LEFT JOIN LiquorLike ll ON ll.liquor = l
        WHERE l.name LIKE %:keyword%
        GROUP BY l.id, l.name, l.category, sc.name, l.imageUrl
    """)
    List<LiquorSummaryDto> findSummaryByNameContaining(@Param("keyword") String keyword);

    // 대분류로 필터링
    @Query("""
        SELECT new com.liquordb.liquor.dto.LiquorSummaryDto(
            l.id,
            l.name,
            l.category,
            sc.name,
            l.imageUrl,
            COUNT(DISTINCT r.id),
            COUNT(DISTINCT ll.id)
        )
        FROM Liquor l
        JOIN l.subcategory sc
        LEFT JOIN l.reviews r
        LEFT JOIN LiquorLike ll ON ll.liquor = l
        WHERE l.category = :category
        GROUP BY l.id, l.name, l.category, sc.name, l.imageUrl
    """)
    List<LiquorSummaryDto> findSummaryByCategory(@Param("category") LiquorCategory category);

    // 소분류로 필터링
    @Query("""
        SELECT new com.liquordb.liquor.dto.LiquorSummaryDto(
            l.id,
            l.name,
            l.subcategory.category,
            l.subcategory.name,
            l.imageUrl,
            COUNT(DISTINCT r.id),
            COUNT(DISTINCT ll.id)
        )
        FROM Liquor l
        LEFT JOIN l.reviews r
        LEFT JOIN LiquorLike ll ON ll.liquor = l
        WHERE l.subcategory = :subcategory
        GROUP BY l.id, l.name, l.subcategory.category, l.subcategory.name, l.imageUrl
    """)
    List<LiquorSummaryDto> findSummaryBySubcategory(@Param("subcategory") LiquorSubcategory subcategory);

    // 삭제되지 않은 주류 단건 조회
    Optional<Liquor> findByIdAndIsHiddenFalse(Long id);
}

package com.liquordb.liquor.repository;

import com.liquordb.liquor.dto.LiquorResponseDto;
import com.liquordb.liquor.dto.LiquorSummaryDto;
import com.liquordb.liquor.entity.Liquor;
import com.liquordb.liquor.entity.LiquorSubCategory;
import com.liquordb.liquor.entity.LiquorCategory;
import com.liquordb.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LiquorRepository extends JpaRepository<Liquor, Long> {

    // 검색
    @Query("""
    SELECT new com.liquordb.liquor.dto.LiquorSummaryDto(
        l.id, l.name, lc.name, l.imageUrl,
        SIZE(l.reviews), SIZE(l.likes)
    )
    FROM Liquor l
    LEFT JOIN l.category lc
    LEFT JOIN l.reviews r
    LEFT JOIN l.likes li
    WHERE l.name LIKE %:keyword%
    GROUP BY l.id, l.name, lc.name, l.imageUrl
    """)
    List<LiquorSummaryDto> findSummaryByNameContaining(@Param("keyword") String keyword);

    // 대분류로 필터링
    @Query("""
    SELECT new com.liquordb.liquor.dto.LiquorSummaryDto(
        l.id, l.name, l.type, l.subcategory, l.imageUrl,
        COUNT(DISTINCT r.id), COUNT(DISTINCT li.id)
    )
    FROM Liquor l
    LEFT JOIN l.reviews r
    LEFT JOIN l.likes li
    WHERE l.type = :type
    GROUP BY l.id, l.name, l.type, l.subcategory, l.imageUrl
    """)
    List<LiquorSummaryDto> findSummaryByType(@Param("type") LiquorCategory type);

    // 소분류로 필터링
    @Query("""
    SELECT new com.liquordb.liquor.dto.LiquorSummaryDto(
        l.id, l.name, l.type, l.subcategory, l.imageUrl,
        COUNT(DISTINCT r.id), COUNT(DISTINCT li.id)
    )
    FROM Liquor l
    LEFT JOIN l.reviews r
    LEFT JOIN l.likes li
    WHERE l.subcategory = :subcategory
    GROUP BY l.id, l.name, l.type, l.subcategory, l.imageUrl
    """)
    List<LiquorSummaryDto> findSummaryBySubcategory(@Param("subcategory") LiquorSubCategory subcategory); // 소분류로 필터링

    @Query("""
    SELECT new com.liquordb.liquor.dto.LiquorSummaryDto(
    l.id,
    l.name,
    lc.name,
    l.imageUrl,
    COUNT(DISTINCT r.id),
    COUNT(DISTINCT li.id)
    )
    FROM Liquor l
    LEFT JOIN l.category lc
    LEFT JOIN l.reviews r
    LEFT JOIN l.likes li
    GROUP BY l.id, l.name, lc.name, l.imageUrl
    """)
    List<LiquorSummaryDto> findAllWithCategoryAndCounts();


    // 삭제되지 않은 주류 단건 조회
    Optional<Liquor> findByIdAndIsDeletedFalse(Long id);

    // 태그로 주류 검색
    @Query("""
    SELECT new com.liquordb.liquor.dto.LiquorSummaryDto(
        l.id, l.name, l.type, l.subcategory, l.imageUrl,
        COUNT(DISTINCT r.id), COUNT(DISTINCT li.id)
    )
    FROM Liquor l
    LEFT JOIN l.reviews r
    LEFT JOIN l.likes li
    JOIN l.tags t
    JOIN l.liquorTags lt
    JOIN lt.tag t
    WHERE t.name = :tagName
    GROUP BY l.id, l.name, l.type, l.subcategory, l.imageUrl
    """)
    List<LiquorSummaryDto> findSummaryByTagName(@Param("tagName") String tagName);
}
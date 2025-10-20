package com.liquordb.repository;

import com.liquordb.entity.Liquor;
import com.liquordb.entity.LiquorSubcategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LiquorRepository extends JpaRepository<Liquor, Long> {

    // 전체 조회
    Page<Liquor> findAllByIsHiddenFalse(Pageable pageable);

    // 검색
    Page<Liquor> findByNameContainingAndIsHiddenFalse(Pageable pageable, String keyword);

    // 대분류로 필터링
    Page<Liquor> findByCategoryAndIsHiddenFalse(Pageable pageable, Liquor.LiquorCategory liquorCategory);

    // 소분류로 필터링
    Page<Liquor> findBySubcategoryAndIsHiddenFalse(Pageable pageable, LiquorSubcategory liquorSubcategory);

    // 삭제되지 않은 주류 단건 조회
    Optional<Liquor> findByIdAndIsHiddenFalse(Long id);
}

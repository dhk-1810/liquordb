package com.liquordb.repository;

import com.liquordb.entity.Liquor;
import com.liquordb.entity.LiquorTag;
import com.liquordb.entity.LiquorTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 주류-태그 연결 저장소입니다.
 */
public interface LiquorTagRepository extends JpaRepository<LiquorTag, LiquorTagId> {

    // 태그 ID로 주류 목록 조회
    List<LiquorTag> findLiquorByTagId(Long tagId);

    // 태그 이름으로 주류 객체들 조회
    @Query("SELECT lt.liquor FROM LiquorTag lt JOIN lt.tag t WHERE t.name = :tagName")
    List<Liquor> findLiquorsByTagName(@Param("tagName") String tagName);

    // 태그 ID로 주류 ID들 조회
    @Query("SELECT lt.liquor.id FROM LiquorTag lt WHERE lt.tag.id IN :tagIds")
    List<Long> findLiquorIdsByTagIds(@Param("tagIds") List<Long> tagIds);

    // 태그 이름으로 주류 ID들 조회
    @Query("SELECT lt.liquor.id FROM LiquorTag lt WHERE lt.tag.name = :tagName")
    List<Long> findLiquorIdsByTagName(@Param("tagName") String tagName);

}
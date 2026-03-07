package com.liquordb.repository;

import com.liquordb.entity.LiquorTag;
import com.liquordb.entity.id.LiquorTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 주류-태그 연결 저장소입니다.
 */
public interface LiquorTagRepository extends JpaRepository<LiquorTag, LiquorTagId> {

    // 특정 주류에 연결된 태그 조회
    @Query("SELECT lt FROM LiquorTag lt WHERE lt.liquor.id = :liquorId")
    List<LiquorTag> findTagsByLiquorId(@Param("liquorId") Long liquorId);

}
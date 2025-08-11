package com.liquordb.liquor.repository;

import com.liquordb.liquor.entity.LiquorTag;
import com.liquordb.liquor.entity.LiquorTagId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LiquorTagRepository extends JpaRepository<LiquorTag, LiquorTagId> {
    List<LiquorTag> findByLiquorId(Long liquorId);
    List<LiquorTag> findByTagId(Long tagId);
    // 태그별로 주류 검색?
}

package com.liquordb.repository;

import com.liquordb.entity.LiquorTag;
import com.liquordb.entity.id.LiquorTagId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiquorTagRepository extends JpaRepository<LiquorTag, LiquorTagId> {

}
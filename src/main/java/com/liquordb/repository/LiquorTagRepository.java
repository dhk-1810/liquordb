package com.liquordb.repository;

import com.liquordb.entity.LiquorTag;
import com.liquordb.entity.id.LiquorTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LiquorTagRepository extends JpaRepository<LiquorTag, LiquorTagId> {

}
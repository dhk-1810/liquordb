package com.liquordb.repository.tag;

import com.liquordb.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long>, CustomTagRepository {
}
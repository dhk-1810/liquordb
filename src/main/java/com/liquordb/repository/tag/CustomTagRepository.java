package com.liquordb.repository.tag;

import com.liquordb.entity.Tag;
import org.springframework.data.domain.Page;

public interface CustomTagRepository {
    Page<Tag> findAll(TagSearchCondition condition);
}

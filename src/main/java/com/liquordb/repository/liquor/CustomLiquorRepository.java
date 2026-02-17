package com.liquordb.repository.liquor;

import com.liquordb.entity.Liquor;
import com.liquordb.repository.liquor.condition.LiquorSearchCondition;
import org.springframework.data.domain.Slice;

public interface CustomLiquorRepository {

    // 전체 조회
    Slice<Liquor> findAll(LiquorSearchCondition condition);
}

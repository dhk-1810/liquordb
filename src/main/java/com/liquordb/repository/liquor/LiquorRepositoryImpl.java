package com.liquordb.repository.liquor;

import com.liquordb.entity.Liquor;
import com.liquordb.repository.liquor.condition.LiquorSearchCondition;
import org.springframework.data.domain.Slice;

public class LiquorRepositoryImpl implements CustomLiquorRepository{

    @Override
    public Slice<Liquor> findAll(LiquorSearchCondition condition) {
        return null;
    }
}

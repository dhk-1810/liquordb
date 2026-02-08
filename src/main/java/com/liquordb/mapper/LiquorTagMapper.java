package com.liquordb.mapper;

import com.liquordb.entity.Liquor;
import com.liquordb.entity.LiquorTag;
import com.liquordb.entity.Tag;

public class LiquorTagMapper {

    public static LiquorTag toEntity(Liquor liquor, Tag tag) {
        return LiquorTag.create(liquor, tag);
    }

}
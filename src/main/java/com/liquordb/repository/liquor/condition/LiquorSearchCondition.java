package com.liquordb.repository.liquor.condition;

import com.liquordb.entity.Liquor;
import com.liquordb.entity.LiquorSubcategory;
import com.liquordb.enums.SortLiquorBy;
import lombok.Builder;

@Builder
public record LiquorSearchCondition (
        Liquor.LiquorCategory category,
        LiquorSubcategory subcategory,
        String keyword,
        Boolean searchDeleted, // null -> 전체 조회, true -> 삭제 리뷰 조회, false -> 활성 리뷰 조회
        Object cursor,
        Long idAfter,
        int limit,
        SortLiquorBy sortBy,
        boolean descending
) {
}

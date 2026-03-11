package com.liquordb.repository.liquor.condition;

import com.liquordb.enums.LiquorCategory;
import com.liquordb.enums.SortLiquorBy;
import lombok.Builder;

import java.util.List;

@Builder
public record LiquorSearchCondition (
        LiquorCategory category,
        Long subcategoryId,
        String keyword,
        boolean searchDeleted,
        List<Long> tagIds,
        Object cursor,
        Long idAfter,
        int limit,
        SortLiquorBy sortBy,
        boolean descending
) {
}

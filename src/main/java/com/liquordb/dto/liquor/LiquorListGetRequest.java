package com.liquordb.dto.liquor;

import com.liquordb.entity.Liquor;
import com.liquordb.entity.LiquorSubcategory;
import com.liquordb.enums.SortLiquorBy;
import com.liquordb.enums.SortDirection;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record LiquorListGetRequest(

        Liquor.LiquorCategory category,

        LiquorSubcategory subcategory,

        @Size(max = 20, message = "키워드는 20자 이내여야 합니다.")
        String keyword,

        Boolean searchDeleted,

        Object cursor,

        Long idAfter,

        @Min(value = 1, message = "최소 1개 이상 조회해야 합니다.")
        @Max(value = 50, message = "최대 50개까지 조회 가능합니다.")
        Integer limit,

        SortLiquorBy sortBy,

        SortDirection sortDirection

) {
}

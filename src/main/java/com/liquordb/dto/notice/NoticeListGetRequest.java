package com.liquordb.dto.notice;

import com.liquordb.enums.SortDirection;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record NoticeListGetRequest (

        Boolean deleted,

        @Min(0)
        Integer page,

        @Min(1) @Max(100)
        Integer limit,

        SortDirection sortDirection
) {
}

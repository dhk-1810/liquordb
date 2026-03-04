package com.liquordb.dto.tag;

import com.liquordb.enums.SortDirection;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record TagListGetRequest(

        String keyword,

        @Min(0)
        Integer page,

        @Min(1) @Max(100)
        Integer limit,

        SortDirection sortDirection
) {
}

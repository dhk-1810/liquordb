package com.liquordb.dto.review;

import com.liquordb.enums.ReviewSortBy;
import com.liquordb.enums.SortDirection;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewListGetRequest (

        @Nullable
        Long cursor,

        @Nullable
        Long idAfter,

        @Min(1) @Max(50)
        int limit,

        @NotNull
        ReviewSortBy sortBy,

        @NotNull
        SortDirection sortDirection
) {
}

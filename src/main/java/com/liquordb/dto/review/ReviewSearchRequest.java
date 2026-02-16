package com.liquordb.dto.review;

import com.liquordb.entity.Review;
import com.liquordb.enums.SortDirection;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ReviewSearchRequest (

        String username,

        Review.ReviewStatus status,

        @Min(0)
        Integer page,

        @Min(1) @Max(100)
        Integer limit,

        SortDirection sortDirection
) {
}

package com.liquordb.dto.comment.request;

import com.liquordb.enums.CommentSortBy;
import com.liquordb.enums.SortDirection;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 커서 페이지네이션 사용
 */
public record CommentListGetRequest (

        @Nullable
        Long cursor,

        @Nullable
        Long idAfter,

        @Min(1) @Max(50)
        Integer limit,

        @NotNull
        CommentSortBy sortBy,

        @NotNull
        SortDirection sortDirection
) {
}

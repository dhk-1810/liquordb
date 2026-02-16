package com.liquordb.dto.comment;

import com.liquordb.entity.Comment;
import com.liquordb.enums.CommentSortBy;
import com.liquordb.enums.SortDirection;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 관리자용, 오프셋 페이지네이션 사용.
 */
public record CommentSearchRequest (

        @NotNull
        String username,

        @Nullable
        Comment.CommentStatus status,

        @Min(0)
        Integer page,

        @Min(1) @Max(100)
        Integer size,

        @NotNull
        CommentSortBy sortBy,

        @NotNull
        SortDirection sortDirection
) {
}

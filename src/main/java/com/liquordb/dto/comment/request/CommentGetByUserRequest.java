package com.liquordb.dto.comment.request;

import com.liquordb.enums.CommentSortBy;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.query.SortDirection;

import java.util.UUID;

public record CommentGetByUserRequest (

        @NotNull
        UUID userId,

        @Nullable
        Long cursor,

        @Min(1) @Max(50)
        int limit,

        @NotNull
        CommentSortBy sortBy,

        @NotNull
        SortDirection sortDirection
) {
}

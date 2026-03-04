package com.liquordb.dto.user;

import com.liquordb.enums.SortDirection;
import com.liquordb.enums.UserStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UserListGetRequest (

        String username,

        String email,

        UserStatus status,

        @Min(0)
        Integer page,

        @Min(1) @Max(100)
        Integer limit,

        SortDirection sortDirection
) {
}

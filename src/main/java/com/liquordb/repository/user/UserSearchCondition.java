package com.liquordb.repository.user;

import com.liquordb.enums.UserStatus;

public record UserSearchCondition (
        String username,
        String email,
        UserStatus status,
        int page,
        int limit,
        boolean descending
) {
}

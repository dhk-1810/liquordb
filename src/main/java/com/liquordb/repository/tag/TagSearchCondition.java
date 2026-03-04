package com.liquordb.repository.tag;

public record TagSearchCondition (
        String keyword,
        int page,
        int limit,
        boolean descending
) {
}

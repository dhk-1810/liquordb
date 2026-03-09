package com.liquordb.repository.notice;

public record NoticeListGetCondition(
        boolean deleted,
        int page,
        int limit,
        boolean descending
) {
}

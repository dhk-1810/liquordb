package com.liquordb.dto.notice;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 공지사항 목록에 표시할 요약된 정보
 */
@Builder
public record NoticeSummaryDto (
        String title,
        boolean isPinned,
        LocalDateTime createdAt
) {

}

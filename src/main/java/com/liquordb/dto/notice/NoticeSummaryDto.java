package com.liquordb.dto.notice;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.LocalDateTime;

/**
 * 공지사항 목록에 표시할 요약된 정보
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class") // 직렬화시 @class 필드 포함
public record NoticeSummaryDto (
        Long id,
        String title,
        boolean isPinned,
        LocalDateTime createdAt
) {

}

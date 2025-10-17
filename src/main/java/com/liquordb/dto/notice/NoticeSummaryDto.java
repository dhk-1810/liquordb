package com.liquordb.dto.notice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 공지사항 목록에 표시할 요약된 정보 DTO입니다.
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeSummaryDto {
    private String title;
    private boolean isPinned;
    private LocalDateTime createdAt;
}

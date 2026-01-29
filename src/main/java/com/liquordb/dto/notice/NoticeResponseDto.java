package com.liquordb.dto.notice;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 공지사항 단건 조회 응답
 */
@Builder
public record NoticeResponseDto (
        Long id,
        String title,
        String content,
        String authorUsername,
        boolean isPinned,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

}

package com.liquordb.dto.notice;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 공지사항 단건 조회 응답
 */
@Builder
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
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

package com.liquordb.mapper;

import com.liquordb.dto.notice.NoticeRequestDto;
import com.liquordb.dto.notice.NoticeResponseDto;
import com.liquordb.dto.notice.NoticeSummaryDto;
import com.liquordb.entity.Notice;

public class NoticeMapper {

    public static Notice toEntity(NoticeRequestDto dto) {
        return Notice.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .isPinned(dto.isPinned())
                .build();
    }

    public static NoticeResponseDto toDto(Notice notice) {
        return NoticeResponseDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                // .isPinned(notice.isPinned())
                .build();
    }

    public static NoticeSummaryDto toSummaryDto(Notice notice) {
        return NoticeSummaryDto.builder()
                .title(notice.getTitle())
                .createdAt(notice.getCreatedAt())
                .isPinned(notice.isPinned())
                .build();
    }
}

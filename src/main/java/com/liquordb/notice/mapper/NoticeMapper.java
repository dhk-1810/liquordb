package com.liquordb.notice.mapper;

import com.liquordb.notice.dto.NoticeRequestDto;
import com.liquordb.notice.dto.NoticeResponseDto;
import com.liquordb.notice.entity.Notice;

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
                .isPinned(notice.isPinned())
                .build();
    }
}

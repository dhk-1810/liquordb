package com.liquordb.mapper;

import com.liquordb.dto.notice.NoticeRequestDto;
import com.liquordb.dto.notice.NoticeResponseDto;
import com.liquordb.dto.notice.NoticeSummaryDto;
import com.liquordb.entity.Notice;
import com.liquordb.entity.User;

import java.util.UUID;

public class NoticeMapper {

    public static Notice toEntity(NoticeRequestDto request, User author) {
        return Notice.create(author, request.title(), request.content());
    }

    public static NoticeResponseDto toDto(Notice notice) {
        return NoticeResponseDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .authorUsername(notice.getAuthor().getUsername())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .isPinned(notice.isPinned())
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

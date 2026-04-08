package com.liquordb.mapper;

import com.liquordb.dto.notice.NoticeRequest;
import com.liquordb.dto.notice.NoticeResponseDto;
import com.liquordb.dto.notice.NoticeSummaryDto;
import com.liquordb.entity.Notice;
import com.liquordb.entity.User;

public class NoticeMapper {

    public static Notice toEntity(NoticeRequest request, User author) {
        return Notice.create(author.getId(), request.title(), request.content());
    }

    public static NoticeResponseDto toDto(Notice notice, String authorName) {
        return NoticeResponseDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .authorUsername(authorName)
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .isPinned(notice.isPinned())
                .build();
    }

    public static NoticeSummaryDto toSummaryDto(Notice notice) {
        return new NoticeSummaryDto(
                notice.getId(),
                notice.getTitle(),
                notice.isPinned(),
                notice.getCreatedAt()
        );
    }
}

package com.liquordb.controller;

import com.liquordb.dto.PageResponse;
import com.liquordb.dto.notice.NoticeResponseDto;
import com.liquordb.dto.notice.NoticeSummaryDto;
import com.liquordb.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponseDto> getNotice(@PathVariable Long noticeId) {
        return ResponseEntity.ok(noticeService.getNotice(noticeId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<NoticeSummaryDto>> getAllNotices(Pageable pageable) {
        return ResponseEntity.ok(noticeService.getAllNotices(pageable));
    }
}

package com.liquordb.controller;

import com.liquordb.dto.notice.NoticeRequestDto;
import com.liquordb.dto.notice.NoticeResponseDto;
import com.liquordb.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자용 공지사항 컨트롤러입니다.
 */

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin/notices")
public class AdminNoticeController {

    private final NoticeService noticeService;

    // 공지사항 등록
    @PostMapping
    public ResponseEntity<NoticeResponseDto> createNotice(@RequestBody NoticeRequestDto dto) {
        return ResponseEntity.ok(noticeService.createNotice(dto));
    }

    // 공지사항 수정
    @PutMapping("/{id}")
    public ResponseEntity<NoticeResponseDto> updateNotice(@PathVariable Long id,
                                                          @RequestBody NoticeRequestDto dto) {
        return ResponseEntity.ok(noticeService.updateNotice(id, dto));
    }

    // 공지사항 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }
}

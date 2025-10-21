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
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/notices")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final NoticeService noticeService;

    // 공지사항 등록
    @PostMapping
    public ResponseEntity<NoticeResponseDto> createNotice(@RequestBody NoticeRequestDto dto) {
        return ResponseEntity.ok(noticeService.create(dto));
    }

    // 공지사항 수정
    @PutMapping("/{id}")
    public ResponseEntity<NoticeResponseDto> updateNotice(@PathVariable Long id,
                                                          @RequestBody NoticeRequestDto dto) {
        return ResponseEntity.ok(noticeService.update(id, dto));
    }

    // 고정 토글
    @PutMapping("{id}")
    public ResponseEntity<NoticeResponseDto> togglePin(@PathVariable Long id){
        return ResponseEntity.ok(noticeService.togglePin(id));
    }

    // 공지사항 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        noticeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package com.liquordb.controller.admin;

import com.liquordb.dto.notice.NoticeRequest;
import com.liquordb.dto.notice.NoticeResponseDto;
import com.liquordb.security.CustomUserDetails;
import com.liquordb.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/notices")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final NoticeService noticeService;

    // 공지사항 등록
    @PostMapping
    public ResponseEntity<NoticeResponseDto> createNotice(
            @RequestBody NoticeRequest dto,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noticeService.create(dto, user.getUserId()));
    }

    // 공지사항 수정
    @PatchMapping("/{noticeId}")
    public ResponseEntity<NoticeResponseDto> updateNotice(@PathVariable Long noticeId, @RequestBody NoticeRequest dto) {
        return ResponseEntity.ok(noticeService.update(noticeId, dto));
    }

    // 고정 토글
    @PatchMapping("/{noticeId}/pin")
    public ResponseEntity<NoticeResponseDto> togglePin(@PathVariable Long noticeId){
        return ResponseEntity.ok(noticeService.togglePin(noticeId));
    }

    // 공지사항 삭제
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long noticeId) {
        noticeService.delete(noticeId);
        return ResponseEntity.noContent().build();
    }
}

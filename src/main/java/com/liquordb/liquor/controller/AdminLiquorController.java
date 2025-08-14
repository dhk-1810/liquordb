package com.liquordb.liquor.controller;

import com.liquordb.liquor.dto.LiquorRequestDto;
import com.liquordb.liquor.service.LiquorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자용 주류 관리 컨트롤러입니다.
 */

@RestController
@RequestMapping("/admin/liquors")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminLiquorController {

    private final LiquorService liquorService;

    // 주류 추가
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createLiquor(@RequestBody LiquorRequestDto requestDto) {
        liquorService.createLiquor(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 주류 수정
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateLiquor(@PathVariable Long id, @RequestBody LiquorRequestDto requestDto) {
        liquorService.updateLiquor(id, requestDto);
        return ResponseEntity.ok().build();
    }

    // 주류 숨기기
    @PatchMapping("/{id}/hide")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> toggleHidden(@PathVariable Long id) {
        liquorService.toggleHidden(id);
        return ResponseEntity.ok().build();
    }

    // 주류 삭제
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLiquor(@PathVariable Long id) {
        liquorService.deleteLiquor(id);
        return ResponseEntity.noContent().build();
    }
}


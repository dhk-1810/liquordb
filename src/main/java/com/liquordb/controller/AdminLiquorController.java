package com.liquordb.controller;

import com.liquordb.dto.liquor.LiquorRequestDto;
import com.liquordb.dto.liquor.LiquorResponseDto;
import com.liquordb.service.LiquorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/liquors")
@RequiredArgsConstructor
public class AdminLiquorController {

    private final LiquorService liquorService;

    // 주류 추가
    @PostMapping
    public ResponseEntity<LiquorResponseDto> createLiquor(@RequestBody LiquorRequestDto requestDto) {
        liquorService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 주류 수정
    @PutMapping("/{id}")
    public ResponseEntity<LiquorResponseDto> updateLiquor(@PathVariable Long id, @RequestBody LiquorRequestDto requestDto) {
        liquorService.update(id, requestDto);
        return ResponseEntity.ok().build();
    }

    // 주류 숨기기
    @PatchMapping("/{id}/hide")
    public ResponseEntity<LiquorResponseDto> toggleHidden(@PathVariable Long id) {
        liquorService.toggleHidden(id);
        return ResponseEntity.ok().build();
    }

    // 주류 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLiquor(@PathVariable Long id) {
        liquorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


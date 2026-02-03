package com.liquordb.controller.admin;

import com.liquordb.dto.liquor.LiquorRequestDto;
import com.liquordb.dto.liquor.LiquorResponseDto;
import com.liquordb.dto.liquor.LiquorUpdateRequestDto;
import com.liquordb.service.LiquorService;
import jakarta.validation.Valid;
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
    public ResponseEntity<LiquorResponseDto> createLiquor(@RequestBody @Valid LiquorRequestDto request) {
        liquorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 주류 수정
    @PatchMapping("/{liquorId}")
    public ResponseEntity<LiquorResponseDto> updateLiquor(@PathVariable Long liquorId, @RequestBody LiquorUpdateRequestDto request) {
        liquorService.update(liquorId, request);
        return ResponseEntity.ok().build();
    }

    // 주류 삭제 (Soft Delete)
    @DeleteMapping("/{liquorId}")
    public ResponseEntity<LiquorResponseDto> toggleHidden(@PathVariable Long liquorId) {
        liquorService.deleteById(liquorId);
        return ResponseEntity.noContent().build();
    }
}


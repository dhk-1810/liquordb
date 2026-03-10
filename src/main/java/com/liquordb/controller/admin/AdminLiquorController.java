package com.liquordb.controller.admin;

import com.liquordb.dto.liquor.LiquorRequest;
import com.liquordb.dto.liquor.LiquorResponseDto;
import com.liquordb.dto.liquor.LiquorUpdateRequest;
import com.liquordb.service.LiquorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/liquors")
public class AdminLiquorController {

    private final LiquorService liquorService;

    // 주류 추가
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<LiquorResponseDto> createLiquor(
            @ModelAttribute(value = "request") @Valid LiquorRequest request,
            @RequestPart(value = "image") MultipartFile image
    ) {
        liquorService.create(request, image);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 주류 수정
    @PatchMapping(path = "/{liquorId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<LiquorResponseDto> updateLiquor(
            @PathVariable Long liquorId,
            @ModelAttribute(value = "request") LiquorUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        liquorService.update(liquorId, request, image);
        return ResponseEntity.ok().build();
    }

    // 주류 삭제 (Soft Delete)
    @DeleteMapping("/{liquorId}")
    public ResponseEntity<LiquorResponseDto> toggleHidden(@PathVariable Long liquorId) {
        liquorService.deleteById(liquorId);
        return ResponseEntity.noContent().build();
    }
}


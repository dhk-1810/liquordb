package com.liquordb.controller.admin;

import com.liquordb.dto.liquor.*;
import com.liquordb.entity.LiquorSubcategory;
import com.liquordb.repository.liquor.LiquorSubcategoryRepository;
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
    private final LiquorSubcategoryRepository liquorSubcategoryRepository;

    // 주류 추가
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<LiquorResponseDto> createLiquor(
            @RequestPart(value = "request") @Valid LiquorRequest request,
            @RequestPart(value = "image") MultipartFile image
    ) {
        liquorService.create(request, image);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 주류 수정
    @PatchMapping(path = "/{liquorId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<LiquorResponseDto> updateLiquor(
            @PathVariable Long liquorId,
            @RequestPart(value = "request") LiquorUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        LiquorResponseDto response = liquorService.update(liquorId, request, image);
        return ResponseEntity.ok(response);
    }

    // 주류 삭제 (Soft Delete)
    @DeleteMapping("/{liquorId}")
    public ResponseEntity<Void> toggleHidden(@PathVariable Long liquorId) {
        liquorService.deleteById(liquorId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/subcategory")
    public ResponseEntity<LiquorSubcategoryResponse> createSubcategory(LiquorSubcategoryRequest request){
        LiquorSubcategoryResponse subcategory = liquorService.createSubcategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(subcategory);
    }

    @DeleteMapping("/subcategory/{id}")
    public ResponseEntity<Void> deleteSubcategory(@PathVariable Long id){
        liquorService.deleteSubcategory(id);
        return ResponseEntity.noContent().build();
    }
}


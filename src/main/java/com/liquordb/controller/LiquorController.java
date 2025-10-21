package com.liquordb.controller;

import com.liquordb.PageResponse;
import com.liquordb.dto.liquor.LiquorResponseDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.entity.LiquorSubcategory;
import com.liquordb.entity.Liquor;
import com.liquordb.entity.User;
import com.liquordb.service.LiquorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 유저용 주류 컨트롤러입니다.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/liquors")
public class LiquorController {

    private final LiquorService liquorService;

    // 1. 주류 목록 조회 (전체 조회 또는 대분류, 소분류별로 필터링)
    @GetMapping
    public ResponseEntity<PageResponse<LiquorSummaryDto>> getLiquorsByFilters(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Liquor.LiquorCategory type,
            @RequestParam(required = false) LiquorSubcategory subcategory,
            Pageable pageable) {
        PageResponse<LiquorSummaryDto> liquor = liquorService.getLiquorsByFilters(user, type, subcategory, pageable);
        return ResponseEntity.ok(liquor);
    }

    // 2. 주류 검색 (이름으로)
    @GetMapping("/search")
    public ResponseEntity<PageResponse<LiquorSummaryDto>> searchLiquors(@AuthenticationPrincipal User user,
                                                                        @RequestParam String keyword,
                                                                        Pageable pageable) {
        PageResponse<LiquorSummaryDto> response = liquorService.searchLiquorsByName(user, keyword, pageable);
        return ResponseEntity.ok(response);
    }

    // 3. 특정 주류 조회
    @GetMapping("/{id}")
    public ResponseEntity<LiquorResponseDto> getLiquorDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        LiquorResponseDto dto = liquorService.getLiquorDetail(id, currentUser);
        return ResponseEntity.ok(dto);
    }

}

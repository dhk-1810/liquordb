package com.liquordb.liquor.controller;

import com.liquordb.liquor.dto.LiquorResponseDto;
import com.liquordb.liquor.dto.LiquorSummaryDto;
import com.liquordb.liquor.entity.LiquorSubcategory;
import com.liquordb.liquor.entity.LiquorCategory;
import com.liquordb.liquor.service.LiquorService;
import com.liquordb.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<List<LiquorSummaryDto>> getLiquorsByFilters(
            @RequestParam(required = false) LiquorCategory type,
            @RequestParam(required = false) LiquorSubcategory subcategory) {
        return ResponseEntity.ok(liquorService.getLiquorsByFilters(type, subcategory));
    }

    // 2. 주류 검색 (이름으로)
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchLiquors(@RequestParam String keyword) {
        List<LiquorSummaryDto> result = liquorService.searchLiquorsByName(keyword);
        Map<String, Object> response = new HashMap<>();
        response.put("count", result.size());
        response.put("liquors", result);
        return ResponseEntity.ok(response);
    }

    // 3. 특정 주류 조회
    @GetMapping("/{id}")
    public ResponseEntity<LiquorResponseDto> getLiquorDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? userDetails.getUserId() : null; // 좋아요
        LiquorResponseDto dto = liquorService.getLiquorDetail(id, userId);
        return ResponseEntity.ok(dto);
    }

}

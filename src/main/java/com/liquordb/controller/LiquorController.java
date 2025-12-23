package com.liquordb.controller;

import com.liquordb.PageResponse;
import com.liquordb.dto.liquor.LiquorLikeResponseDto;
import com.liquordb.dto.liquor.LiquorResponseDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.entity.LiquorSubcategory;
import com.liquordb.entity.Liquor;
import com.liquordb.entity.User;
import com.liquordb.service.LiquorLikeService;
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

@RestController
@RequestMapping("/api/liquors")
@RequiredArgsConstructor
public class LiquorController {

    private final LiquorService liquorService;
    private final LiquorLikeService liquorLikeService;

    // 주류 목록 조회 (전체 조회 또는 대분류, 소분류별로 필터링)
    @GetMapping
    public ResponseEntity<PageResponse<LiquorSummaryDto>> getLiquorsByFilters(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Liquor.LiquorCategory type,
            @RequestParam(required = false) LiquorSubcategory subcategory,
            Pageable pageable) {
        PageResponse<LiquorSummaryDto> liquor = liquorService.getLiquorsByFilters(user, type, subcategory, pageable);
        return ResponseEntity.ok(liquor);
    }

    // 주류 검색 (이름으로)
    @GetMapping("/search")
    public ResponseEntity<PageResponse<LiquorSummaryDto>> searchLiquors(@AuthenticationPrincipal User user,
                                                                        @RequestParam String keyword,
                                                                        Pageable pageable) {
        PageResponse<LiquorSummaryDto> response = liquorService.searchLiquorsByName(user, keyword, pageable);
        return ResponseEntity.ok(response);
    }

    // 주류 단건 조회
    @GetMapping("/{liquorId}")
    public ResponseEntity<LiquorResponseDto> getLiquorDetail(
            @PathVariable Long liquorId,
            @AuthenticationPrincipal User currentUser) {

        LiquorResponseDto dto = liquorService.getLiquorDetail(liquorId, currentUser);
        return ResponseEntity.ok(dto);
    }

    // 주류 좋아요 토글 (누르기/취소)
    @PostMapping("/{liquorId}/like")
    public ResponseEntity<LiquorLikeResponseDto> toggleLike(@PathVariable Long liquorId,
                                                            @RequestParam UUID userId) {
        LiquorLikeResponseDto response = liquorLikeService.toggleLike(userId, liquorId);
        return ResponseEntity.ok(response);
    }

}

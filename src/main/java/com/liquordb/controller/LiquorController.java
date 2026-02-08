package com.liquordb.controller;

import com.liquordb.PageResponse;
import com.liquordb.dto.LikeResponseDto;
import com.liquordb.dto.liquor.LiquorResponseDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.entity.LiquorSubcategory;
import com.liquordb.entity.Liquor;
import com.liquordb.security.CustomUserDetails;
import com.liquordb.service.LiquorLikeService;
import com.liquordb.service.LiquorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/liquors")
public class LiquorController {

    private final LiquorService liquorService;
    private final LiquorLikeService liquorLikeService;

    // 주류 목록 조회 (전체 조회 또는 대분류, 소분류별로 필터링)
    @GetMapping
    public ResponseEntity<PageResponse<LiquorSummaryDto>> getLiquorsByFilters(
            @RequestParam(required = false) Liquor.LiquorCategory category,
            @RequestParam(required = false) LiquorSubcategory subcategory,
            @AuthenticationPrincipal CustomUserDetails user,
            Pageable pageable
    ) {
        UUID userId = (user != null) ? user.getUserId() : null;
        PageResponse<LiquorSummaryDto> liquor = liquorService.getLiquorsByFilters(category, subcategory, userId, pageable);
        return ResponseEntity.ok(liquor);
    }

    // 주류 검색 (이름으로)
    @GetMapping("/search")
    public ResponseEntity<PageResponse<LiquorSummaryDto>> searchLiquors(
            @RequestParam String keyword,
            @AuthenticationPrincipal CustomUserDetails user,
            Pageable pageable
    ) {
        UUID userId = (user != null) ? user.getUserId() : null;
        PageResponse<LiquorSummaryDto> response = liquorService.searchLiquorsByName(keyword, userId, pageable);
        return ResponseEntity.ok(response);
    }

    // 주류 단건 조회
    @GetMapping("/{liquorId}")
    public ResponseEntity<LiquorResponseDto> getLiquorDetail(
            @PathVariable Long liquorId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        UUID userId = (user != null) ? user.getUserId() : null;
        LiquorResponseDto dto = liquorService.getLiquorDetail(liquorId, userId);
        return ResponseEntity.ok(dto);
    }

    // 좋아요
    @PostMapping("/{liquorId}/like")
    public ResponseEntity<LikeResponseDto> like(
            @PathVariable Long liquorId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        LikeResponseDto response = liquorLikeService.like(liquorId, user.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 좋아요 취소
    @DeleteMapping("/{liquorId}/cancel-like")
    public ResponseEntity<LikeResponseDto> cancelLike(
            @PathVariable Long liquorId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        LikeResponseDto response = liquorLikeService.cancelLike(liquorId, user.getUserId());
        return ResponseEntity.ok(response);
    }



}

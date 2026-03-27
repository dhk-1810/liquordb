package com.liquordb.controller;

import com.liquordb.dto.CursorPageResponse;
import com.liquordb.dto.liquor.LiquorListGetRequest;
import com.liquordb.dto.liquor.LiquorResponseDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.enums.Role;
import com.liquordb.security.CustomUserDetails;
import com.liquordb.service.LiquorLikeService;
import com.liquordb.service.LiquorRankingService;
import com.liquordb.service.LiquorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/liquors")
public class LiquorController {

    private final LiquorService liquorService;
    private final LiquorRankingService rankingService;
    private final LiquorLikeService liquorLikeService;

    // 인기 주류 조회
    @GetMapping("/trending")
    public ResponseEntity<List<LiquorSummaryDto>> getTrending(){
        List<LiquorSummaryDto> trending = rankingService.getTopRankings(10);
        return ResponseEntity.ok(trending);
    }

    // 주류 목록 조회 (전체 조회 또는 대분류, 소분류별로 필터링)
    @GetMapping
    public ResponseEntity<CursorPageResponse<LiquorSummaryDto>> getAll(
            @ModelAttribute LiquorListGetRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        UUID viewerId = (user != null) ? user.id() : null;
        boolean isViewerRoleAdmin = user != null && (user.dto().role() == Role.ADMIN);
        CursorPageResponse<LiquorSummaryDto> liquor = liquorService.getAll(request, viewerId, isViewerRoleAdmin);
        return ResponseEntity.ok(liquor);
    }

    // 주류 단건 조회
    @GetMapping("/{liquorId}")
    public ResponseEntity<LiquorResponseDto> getLiquorDetail(
            @PathVariable Long liquorId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        UUID userId = (user != null) ? user.id() : null;
        LiquorResponseDto dto = liquorService.getLiquorDetail(liquorId, userId);
        return ResponseEntity.ok(dto);
    }

    // 좋아요
    @PostMapping("/{liquorId}/like")
    public ResponseEntity<Void> like(
            @PathVariable Long liquorId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        liquorLikeService.like(liquorId, user.id());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 좋아요 취소
    @DeleteMapping("/{liquorId}/cancel-like")
    public ResponseEntity<Void> cancelLike(
            @PathVariable Long liquorId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        liquorLikeService.cancelLike(liquorId, user.id());
        return ResponseEntity.noContent().build();
    }

}

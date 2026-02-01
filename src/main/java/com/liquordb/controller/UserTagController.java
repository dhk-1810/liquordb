package com.liquordb.controller;

import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.service.UserTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/tags")
@RequiredArgsConstructor
public class UserTagController {

    private final UserTagService userTagService;

    // 유저 기준 태그 목록 조회
    @GetMapping("/user/{userId}")
    public List<TagResponseDto> getPreferredTags(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "false") boolean showAll
    ) {
        return userTagService.getByUserId(userId, showAll);
    }

    // 선호 태그 기반 주류 추천
    @GetMapping("/user/{userId}/preferred-liquors")
    public ResponseEntity<List<LiquorSummaryDto>> getPreferredLiquors(@PathVariable UUID userId) {

        List<LiquorSummaryDto> liquors = userTagService.getLiquorsByUserTags(userId);
        return ResponseEntity.ok(liquors);
    }
}

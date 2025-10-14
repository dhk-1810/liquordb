package com.liquordb.controller;

import com.liquordb.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    // 특정 주류에 연결된 태그 이름 목록 반환
    @GetMapping("/liquor/{liquorId}")
    public ResponseEntity<List<String>> getTagsForLiquor(@PathVariable Long liquorId) {
        return ResponseEntity.ok(tagService.getTagsForLiquor(liquorId));
    }

    // 특정 유저의 태그 목록 조회
    @GetMapping("/user/{userId}")
    public List<String> getPreferredTags(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "false") boolean showAll
    ) {
        return tagService.getPreferredTagsForUser(userId, showAll);
    }
}

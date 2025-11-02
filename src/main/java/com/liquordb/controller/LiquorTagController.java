package com.liquordb.controller;

import com.liquordb.dto.liquor.LiquorResponseDto;
import com.liquordb.dto.liquor.LiquorTagRequestDto;
import com.liquordb.dto.liquor.LiquorTagResponseDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.Liquor;
import com.liquordb.entity.Tag;
import com.liquordb.entity.User;
import com.liquordb.service.LiquorTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/liquors/tags")
public class LiquorTagController {

    private final LiquorTagService liquorTagService;

    // 태그 이름으로 주류 검색
    @GetMapping("/search/name/{tagName}")
    public ResponseEntity<List<LiquorResponseDto>> getLiquorsByTagName(@PathVariable String tagName,
                                                                       @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(liquorTagService.getLiquorsByTagName(tagName, user));
    }

    // 태그 ID로 주류 검색
    @GetMapping("/search/id/{tagId}")
    public ResponseEntity<List<LiquorResponseDto>> getLiquorsByTagId(@PathVariable Long tagId,
                                                                     @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(liquorTagService.getLiquorsByTagId(tagId, user));
    }

    // 주류 ID로 연결된 태그 검색
    @GetMapping("/liquor/{liquorId}")
    public ResponseEntity<List<LiquorTagResponseDto>> getTagsByLiquorId(@PathVariable Long liquorId) {
        return ResponseEntity.ok(liquorTagService.getTagsByLiquorId(liquorId));
    }

    // 주류에 태그 추가
    @PostMapping
    public ResponseEntity<LiquorTagResponseDto> createLiquorTag(@RequestBody LiquorTagRequestDto requestDto) {
        LiquorTagResponseDto responseDto = liquorTagService.addLiquorTag(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
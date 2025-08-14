package com.liquordb.liquor.controller;

import com.liquordb.liquor.dto.LiquorTagRequestDto;
import com.liquordb.liquor.dto.LiquorTagResponseDto;
import com.liquordb.liquor.entity.Liquor;
import com.liquordb.liquor.service.LiquorTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/liquors/tags")
public class LiquorTagController {

    private final LiquorTagService liquorTagService;

    // 태그 이름으로 주류 검색
    @GetMapping("/search/name/{tagName}")
    public ResponseEntity<List<Liquor>> getLiquorsByTagName(@PathVariable String tagName) {
        return ResponseEntity.ok(liquorTagService.getLiquorsByTagName(tagName));
    }

    // 태그 ID로 주류 검색
    @GetMapping("/search/id/{tagId}")
    public ResponseEntity<List<Liquor>> getLiquorsByTagId(@PathVariable Long tagId) {
        return ResponseEntity.ok(liquorTagService.getLiquorsByTagId(tagId));
    }

    // 주류에 태그 추가
    @PostMapping
    public ResponseEntity<LiquorTagResponseDto> createLiquorTag(@RequestBody LiquorTagRequestDto requestDto) {
        LiquorTagResponseDto responseDto = liquorTagService.addLiquorTag(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
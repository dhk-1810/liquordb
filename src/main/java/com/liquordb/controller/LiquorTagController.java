package com.liquordb.controller;

import com.liquordb.dto.tag.LiquorTagRequest;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.service.LiquorTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/liquors")
public class LiquorTagController {

    private final LiquorTagService liquorTagService;

    // 주류 ID로 연결된 태그 검색
    @GetMapping("/{liquorId}/tags")
    public ResponseEntity<List<TagResponseDto>> getTagsByLiquorId(@PathVariable Long liquorId) {
        return ResponseEntity.ok(liquorTagService.getTagsByLiquorId(liquorId));
    }

    // 주류에 태그 추가
    @PostMapping("/{liquorId}/tags")
    public ResponseEntity<TagResponseDto> createLiquorTag(@RequestBody LiquorTagRequest request) {
        TagResponseDto responseDto = liquorTagService.add(request);
        return ResponseEntity.ok(responseDto);
    }
}
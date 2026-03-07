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

    // 주류에 태그 추가
    @PostMapping("/{liquorId}/tags")
    public ResponseEntity<TagResponseDto> createLiquorTag(@RequestBody LiquorTagRequest request) {
        TagResponseDto responseDto = liquorTagService.add(request);
        return ResponseEntity.ok(responseDto);
    }
}
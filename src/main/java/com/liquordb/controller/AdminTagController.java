package com.liquordb.controller;

import com.liquordb.dto.tag.TagRequestDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/tags")
@RequiredArgsConstructor
public class AdminTagController {

    private final TagService tagService;

    // 태그 등록
    @PostMapping
    public ResponseEntity<TagResponseDto> create(@RequestBody TagRequestDto requestDto) {
        TagResponseDto createdTag = tagService.create(requestDto);
        return ResponseEntity.ok(createdTag);
    }

    // 태그 이름 변경
    @PatchMapping("/{id}")
    public ResponseEntity<TagResponseDto> update(@PathVariable Long id,
                                                 @RequestBody TagRequestDto requestDto) {
        TagResponseDto updatedTag = tagService.rename(id, requestDto);
        return ResponseEntity.ok(updatedTag);
    }

    // 태그 삭제
    @PatchMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
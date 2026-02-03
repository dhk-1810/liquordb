package com.liquordb.controller;

import com.liquordb.dto.tag.TagRequestDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/tags")
@RequiredArgsConstructor
public class AdminTagController {

    private final TagService tagService;

    // 태그 등록
    @PostMapping
    public ResponseEntity<TagResponseDto> create(@RequestBody @Valid TagRequestDto requestDto) {
        TagResponseDto createdTag = tagService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTag);
    }

    // 태그 전체 조회
    // TODO 페이지네이션
    @GetMapping
    public ResponseEntity<List<TagResponseDto>> getAll() {
        List<TagResponseDto> response = tagService.findAll();
        return ResponseEntity.ok().body(response);
    }

    // 태그 이름 변경
    @PatchMapping("/{id}")
    public ResponseEntity<TagResponseDto> rename(
            @PathVariable Long id,
            @RequestBody @Valid TagRequestDto requestDto
    ) {
        TagResponseDto updatedTag = tagService.rename(id, requestDto);
        return ResponseEntity.ok(updatedTag);
    }

    // 태그 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
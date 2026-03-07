package com.liquordb.controller.admin;

import com.liquordb.dto.PageResponse;
import com.liquordb.dto.tag.TagListGetRequest;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/tags")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTagController {

    private final TagService tagService;

    // 태그 전체 조회
    @GetMapping
    public ResponseEntity<PageResponse<TagResponseDto>> getAll(TagListGetRequest request) {
        PageResponse<TagResponseDto> response = tagService.getAll(request);
        return ResponseEntity.ok().body(response);
    }

}
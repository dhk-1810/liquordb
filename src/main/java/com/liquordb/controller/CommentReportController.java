package com.liquordb.controller;

import com.liquordb.dto.report.CommentReportRequestDto;
import com.liquordb.dto.report.CommentReportResponseDto;
import com.liquordb.security.CustomUserDetails;
import com.liquordb.service.CommentReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports/comments")
public class CommentReportController {

    private final CommentReportService commentReportService;

    @PostMapping
    public ResponseEntity<CommentReportResponseDto> create(
            @RequestBody @Valid CommentReportRequestDto request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        CommentReportResponseDto response = commentReportService.create(request, user.getUserId(), user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

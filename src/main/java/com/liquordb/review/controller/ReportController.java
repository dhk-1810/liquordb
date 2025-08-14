package com.liquordb.review.controller;

import com.liquordb.review.dto.ReportRequestDto;
import com.liquordb.review.service.ReportService;
import com.liquordb.review.service.ReviewService;
import com.liquordb.user.UserValidator;
import com.liquordb.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<String> createReport(@RequestBody ReportRequestDto dto) {
        reportService.report(dto);
        return ResponseEntity.ok("신고가 접수되었습니다.");
    }
}
package com.liquordb.controller;

import com.liquordb.dto.report.ReportRequestDto;
import com.liquordb.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody ReportRequestDto dto) {
        reportService.create(dto);
        return ResponseEntity.ok("신고가 접수되었습니다.");
    }
}
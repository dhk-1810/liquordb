package com.liquordb.controller;

import com.liquordb.dto.report.ReportRequestDto;
import com.liquordb.dto.report.ReportResponseDto;
import com.liquordb.security.CustomUserDetails;
import com.liquordb.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponseDto> create(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    @RequestBody @Valid ReportRequestDto request) {
        ReportResponseDto response = reportService.create(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
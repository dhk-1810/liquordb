package com.liquordb.controller;

import com.liquordb.dto.report.ReviewReportRequestDto;
import com.liquordb.dto.report.ReviewReportResponseDto;
import com.liquordb.security.CustomUserDetails;
import com.liquordb.service.ReviewReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports/reviews")
public class ReviewReportController {

    private final ReviewReportService reportService;

    @PostMapping
    public ResponseEntity<ReviewReportResponseDto> create(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                          @RequestBody @Valid ReviewReportRequestDto request) {
        ReviewReportResponseDto response = reportService.create(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
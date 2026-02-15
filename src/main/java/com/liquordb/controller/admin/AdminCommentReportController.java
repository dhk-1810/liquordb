package com.liquordb.controller.admin;

import com.liquordb.dto.PageResponse;
import com.liquordb.dto.report.CommentReportResponseDto;
import com.liquordb.dto.report.CommentReportSummaryDto;
import com.liquordb.enums.ReportStatus;
import com.liquordb.service.CommentReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/reports/comments")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCommentReportController {

    private final CommentReportService commentReportService;

    @GetMapping
    public ResponseEntity<PageResponse<CommentReportSummaryDto>> getAll(ReportStatus status, Pageable pageable) {
        return ResponseEntity.ok(commentReportService.getAll(status, pageable));
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<CommentReportResponseDto> getById(@PathVariable Long reportId) {
        return ResponseEntity.ok(commentReportService.getById(reportId));
    }

}

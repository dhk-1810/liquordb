package com.liquordb.service;

import com.liquordb.PageResponse;
import com.liquordb.ReportManager;
import com.liquordb.dto.report.CommentReportRequestDto;
import com.liquordb.dto.report.CommentReportResponseDto;
import com.liquordb.dto.report.CommentReportSummaryDto;
import com.liquordb.entity.*;
import com.liquordb.enums.ReportStatus;
import com.liquordb.exception.comment.CommentNotFoundException;
import com.liquordb.exception.report.CommentReportAlreadyExistsException;
import com.liquordb.exception.report.CommentReportNotFoundException;
import com.liquordb.exception.review.ReviewNotFoundException;
import com.liquordb.mapper.CommentReportMapper;
import com.liquordb.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentReportService {

    private final CommentReportRepository commentReportRepository;
    private final CommentRepository commentRepository;
    private final ReportManager reportManager;

    private static final int REPORT_THRESHOLD = 3;

    // 신고 생성
    public CommentReportResponseDto create(CommentReportRequestDto request, UUID reporterId, String reporterUsername) {

        Long commentId = request.commentId();
        Comment comment = commentRepository.findByIdAndStatus(commentId, Comment.CommentStatus.ACTIVE)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        // 중복 신고 방지
        boolean exists = commentReportRepository.existsByCommentIdAndUser_Id(commentId, reporterId);
        if (exists) {
            throw new CommentReportAlreadyExistsException(commentId, reporterId);
        }

        // 신고 저장
        CommentReport report = commentReportRepository
                .save(CommentReportMapper.toEntity(comment, request.reason(), reporterId, reporterUsername));

        // 누적 신고 수 확인 + 조건 충족시 숨기기 처리
        long count = commentReportRepository.countByComment_Id(commentId);
        if (count >= REPORT_THRESHOLD) {
            hideComment(commentId);
        }

        return CommentReportMapper.toDto(report);
    }

    // 자동 숨기기 처리 (3건 이상 신고 접수되면)
    private void hideComment(Long reviewId) {
        Comment comment = commentRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
        comment.hide(LocalDateTime.now());
        commentRepository.save(comment);
    }

    /**
     * 관리자용
     */

    // 신고 목록 조회
    @Transactional(readOnly = true)
    public PageResponse<CommentReportSummaryDto> getAll(ReportStatus status, Pageable pageable) {
        Page<CommentReport> reports = commentReportRepository.findAllByStatus(status, pageable);
        Page<CommentReportSummaryDto> response = reports.map(CommentReportMapper::toSummaryDto);
        return PageResponse.from(response);
    }

    // 신고 단건 조회
    @Transactional(readOnly = true)
    public CommentReportResponseDto getById(Long id) {
        CommentReport commentReport = commentReportRepository.findById(id)
                .orElseThrow(() -> new CommentReportNotFoundException(id));
        return CommentReportMapper.toDto(commentReport);
    }

    // 신고 승인.
    // TODO 신고 누적건수에 따라 유저활동을 일시제한, 영구제한함.
    @Transactional
    public CommentReportResponseDto approveById(Long id) {
        CommentReport report = commentReportRepository.findById(id)
                .orElseThrow(() -> new CommentReportNotFoundException(id));
        report.approve();
        commentReportRepository.save(report);

        User user = report.getComment().getUser();
        reportManager.processUserPenalty(user);

        return CommentReportMapper.toDto(report);
    }

    // 신고 반려
    public CommentReportResponseDto rejectById(Long id) {
        CommentReport report = commentReportRepository.findById(id)
                .orElseThrow(() -> new CommentReportNotFoundException(id));

        report.getComment().unhide();
        report.reject();

        commentReportRepository.save(report);
        return CommentReportMapper.toDto(report);
    }
}

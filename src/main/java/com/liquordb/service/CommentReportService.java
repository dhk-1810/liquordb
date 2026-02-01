package com.liquordb.service;

import com.liquordb.ReportManager;
import com.liquordb.dto.report.CommentReportRequestDto;
import com.liquordb.dto.report.CommentReportResponseDto;
import com.liquordb.entity.*;
import com.liquordb.exception.report.CommentReportNotFoundException;
import com.liquordb.exception.report.ReportNotFoundException;
import com.liquordb.exception.review.ReviewNotFoundException;
import com.liquordb.exception.user.UserNotFoundException;
import com.liquordb.mapper.CommentReportMapper;
import com.liquordb.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentReportService {

    private final CommentReportRepository commentReportRepository;
    private final CommentRepository commentRepository;
    private final CommentReportMapper commentReportMapper;
    private final UserRepository userRepository;
    private final ReportManager reportManager;

    private static final int REPORT_THRESHOLD = 3;

    // 신고 생성
    public CommentReportResponseDto create(UUID requestUserId, CommentReportRequestDto request) {

        User requestUser = userRepository.findById(requestUserId)
                .orElseThrow(() -> new UserNotFoundException(requestUserId));

        // 중복 신고 방지
        boolean exists = commentReportRepository.existsByCommentIdAndUser_Id(request.commentId(), requestUserId);
        if (exists) {
            throw new IllegalArgumentException("이미 신고한 대상입니다."); // TODO 커스텀예외
        }

        // 신고 저장
        CommentReport report = commentReportRepository.save(commentReportMapper.toEntity(request, requestUser));

        // 누적 신고 수 확인 + 조건 충족시 숨기기 처리
        long count = commentReportRepository.countByComment_Id(request.commentId());
        if (count >= REPORT_THRESHOLD) {
            hideComment(request.commentId());
        }

        return commentReportMapper.toDto(report);
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

        return commentReportMapper.toDto(report);
    }

    // 신고 반려
    public CommentReportResponseDto rejectById(Long id) {
        CommentReport report = commentReportRepository.findById(id)
                .orElseThrow(() -> new ReportNotFoundException(id));

        report.getComment().unhide();
        report.reject();

        commentReportRepository.save(report);
        return commentReportMapper.toDto(report);
    }
}

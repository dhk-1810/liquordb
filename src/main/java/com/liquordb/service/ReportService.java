package com.liquordb.service;

import com.liquordb.dto.report.ReportRequestDto;
import com.liquordb.dto.report.ReportResponseDto;
import com.liquordb.entity.*;
import com.liquordb.exception.NotFoundException;
import com.liquordb.mapper.ReportMapper;
import com.liquordb.repository.CommentRepository;
import com.liquordb.repository.ReportRepository;
import com.liquordb.repository.ReviewRepository;
import com.liquordb.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;

    private static final int REPORT_THRESHOLD = 3;
    private final UserRepository userRepository;

    // 신고 생성
    public ReportResponseDto create(ReportRequestDto dto) {
        // 중복 신고 방지
        boolean exists = reportRepository.existsByTargetTypeAndTargetIdAndUserId(
                dto.getTargetType(),
                dto.getTargetId(),
                dto.getUserId()
        );
        if (exists) {
            throw new IllegalArgumentException("이미 신고한 대상입니다.");
        }

        // 신고 저장
        Report report = reportRepository.save(ReportMapper.toEntity(dto));

        // 누적 신고 수 확인 + 조건 충족시 숨기기 처리
        long count = reportRepository.countByTargetTypeAndTargetId(
                dto.getTargetType(),
                dto.getTargetId()
        );
        if (count >= REPORT_THRESHOLD) {
            hideTarget(dto.getTargetId(), dto.getTargetType());
        }

        return ReportMapper.toDto(report);
    }

    // 자동 숨기기 처리 (3건 이상 신고 접수되면)
    private void hideTarget(Long targetId, ReportTargetType targetType) {
        switch (targetType) {
            case REVIEW -> {
                Review review = reviewRepository.findById(targetId)
                        .orElseThrow(() -> new NotFoundException("리뷰를 찾을 수 없습니다."));
                review.setHidden(true); // hidden 필드가 boolean 타입이라고 가정
                reviewRepository.save(review);
            }
            case COMMENT -> {
                Comment comment = commentRepository.findById(targetId)
                        .orElseThrow(() -> new NotFoundException("댓글을 찾을 수 없습니다."));
                comment.setHidden(true); // hidden 필드가 boolean 타입이라고 가정
                commentRepository.save(comment);
            }
        }
    }

    /**
     * 관리자용
     */
    // 신고 승인.
    // TODO 신고 누적건수에 따라 유저활동을 경고, 일시제한, 영구제한함.
    @Transactional
    public ReportResponseDto approveById(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 신고입니다."));
        report.setApproved(true);
        report.setApprovedAt(LocalDateTime.now());

        User user = switch (report.getTargetType()) {
            case COMMENT -> report.getComment().getUser();
            case REVIEW -> report.getReview().getUser();
            default -> throw new IllegalArgumentException("지원하지 않는 신고 대상 타입입니다.");
        };


        if (user.getReportCount() >= 5) {
            user.setStatus(UserStatus.BANNED);
        } else if (user.getReportCount() >= 3) {
            user.setStatus(UserStatus.RESTRICTED);
        } else {
            user.setStatus(UserStatus.WARNED);
        }

        reportRepository.save(report);
        userRepository.save(user);
        return ReportMapper.toDto(report);
    }

    // 신고 반려
    public ReportResponseDto rejectById(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 신고입니다."));
        if (report.getTargetType() == ReportTargetType.COMMENT) {
            report.getComment().setHidden(false);
        } else {
            report.getReview().setHidden(false);
        }
        report.setApproved(false);
        reportRepository.save(report);
        return ReportMapper.toDto(report);
    }

}

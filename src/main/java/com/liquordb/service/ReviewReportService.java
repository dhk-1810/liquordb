package com.liquordb.service;

import com.liquordb.dto.report.ReviewReportRequestDto;
import com.liquordb.dto.report.ReviewReportResponseDto;
import com.liquordb.entity.*;
import com.liquordb.exception.report.ReportNotFoundException;
import com.liquordb.exception.ReviewNotFoundException;
import com.liquordb.exception.user.UserNotFoundException;
import com.liquordb.mapper.ReviewReportMapper;
import com.liquordb.repository.ReviewReportRepository;
import com.liquordb.repository.ReviewRepository;
import com.liquordb.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewReportService {

    private final ReviewReportRepository reviewReportRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewReportMapper reviewReportMapper;

    private static final int REPORT_THRESHOLD = 3;

    // 신고 생성
    public ReviewReportResponseDto create(UUID requestUserId, ReviewReportRequestDto request) {

        User requestUser = userRepository.findById(requestUserId)
                .orElseThrow(() -> new UserNotFoundException(requestUserId));

        // 중복 신고 방지
        boolean exists = reviewReportRepository.existsByReviewIdAndUser_Id(request.reviewId(), requestUserId);
        if (exists) {
            throw new IllegalArgumentException("이미 신고한 대상입니다."); // TODO 커스텀예외
        }

        // 신고 저장
        ReviewReport report = reviewReportRepository.save(reviewReportMapper.toEntity(request, requestUser));

        // 누적 신고 수 확인 + 조건 충족시 숨기기 처리
        long count = reviewReportRepository.countByReview_Id(request.reviewId());
        if (count >= REPORT_THRESHOLD) {
            hideReport(request.reviewId());
        }

        return reviewReportMapper.toDto(report);
    }

    // 자동 숨기기 처리 (3건 이상 신고 접수되면)
    private void hideReport(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
        review.hide(LocalDateTime.now());
        reviewRepository.save(review);
    }

    /**
     * 관리자용
     */
    // 신고 승인.
    @Transactional
    public ReviewReportResponseDto approveById(Long id) {
        ReviewReport report = reviewReportRepository.findById(id)
                .orElseThrow(() -> new ReportNotFoundException(id));
        report.approve();

        User user = report.getReview().getUser();

        if (user.getReportCount() >= 5) {
            user.ban();
        } else if (user.getReportCount() >= 3) {
            user.suspend(LocalDateTime.now().plusDays(7));
        }

        reviewReportRepository.save(report);
        userRepository.save(user);
        return reviewReportMapper.toDto(report);
    }

    // 신고 반려
    public ReviewReportResponseDto rejectById(Long id) {
        ReviewReport report = reviewReportRepository.findById(id)
                .orElseThrow(() -> new ReportNotFoundException(id));

        report.getReview().unhide();
        report.reject();

        reviewReportRepository.save(report);
        return reviewReportMapper.toDto(report);
    }
}

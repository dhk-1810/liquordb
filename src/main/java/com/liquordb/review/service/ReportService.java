package com.liquordb.review.service;

import com.liquordb.review.entity.Comment;
import com.liquordb.review.entity.Report;
import com.liquordb.review.entity.ReportTargetType;
import com.liquordb.review.entity.Review;
import com.liquordb.review.repository.CommentRepository;
import com.liquordb.review.repository.ReportRepository;
import com.liquordb.review.repository.ReviewRepository;
import com.liquordb.user.entity.User;
import com.liquordb.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;

    private static final int REPORT_THRESHOLD = 3;

    public void report(User reporter, Long targetId, ReportTargetType targetType, String reason) {
        // 중복 신고 방지
        if (reportRepository.existsByReporterAndTargetIdAndTargetType(reporter, targetId, targetType)) {
            throw new IllegalArgumentException("이미 신고한 대상입니다.");
        }

        // 신고 저장
        Report report = Report.builder()
                .reporter(reporter)
                .targetId(targetId)
                .targetType(targetType)
                .reason(reason)
                .reportedAt(LocalDateTime.now())
                .build();
        reportRepository.save(report);

        // 누적 신고 수 확인
        long count = reportRepository.countByTargetIdAndTargetType(targetId, targetType);
        if (count >= REPORT_THRESHOLD) {
            hideTarget(targetId, targetType);
        }
    }

    // 자동 숨기기 처리 (3건 이상 신고 접수되면)
    private void hideTarget(Long targetId, ReportTargetType targetType) {
        if (targetType == ReportTargetType.REVIEW) {
            Review review = reviewRepository.findById(targetId)
                    .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
            review.setHidden(true);
            reviewRepository.save(review);
        } else if (targetType == ReportTargetType.COMMENT) {
            Comment comment = commentRepository.findById(targetId)
                    .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
            comment.setHidden(true);
            commentRepository.save(comment);
        }
    }
}

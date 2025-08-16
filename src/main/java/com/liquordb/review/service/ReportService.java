package com.liquordb.review.service;

import com.liquordb.review.dto.ReportRequestDto;
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

    private final ReportRepository reportRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;

    private static final int REPORT_THRESHOLD = 3;

    // 신고 생성
    public void report(ReportRequestDto dto) {
        // 중복 신고 방지
        boolean exists = reportRepository.existsByTargetTypeAndTargetIdAndUserId(
                dto.getTargetType(),
                dto.getTargetId(),
                dto.getUserId()
        );
        if (exists) {
            throw new RuntimeException("이미 신고한 대상입니다.");
        }

        // 신고 저장
        Report report = Report.builder()
                .userId(dto.getUserId())
                .targetId(dto.getTargetId())
                .targetType(dto.getTargetType())
                .reason(dto.getReason())
                .createdAt(LocalDateTime.now())
                .build();
        reportRepository.save(report);

        // 누적 신고 수 확인 및 숨기기 처리
        long count = reportRepository.countByTargetTypeAndTargetId(
                dto.getTargetType(),
                dto.getTargetId()
        );

        if (count >= REPORT_THRESHOLD) {
            hideTarget(dto.getTargetId(), dto.getTargetType());
        }
    }

    // 자동 숨기기 처리 (3건 이상 신고 접수되면)
    private void hideTarget(Long targetId, ReportTargetType targetType) {
        switch (targetType) {
            case REVIEW -> {
                Review review = reviewRepository.findById(targetId)
                        .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
                review.setHidden(true); // hidden 필드가 boolean 타입이라고 가정
                reviewRepository.save(review);
            }
            case COMMENT -> {
                Comment comment = commentRepository.findById(targetId)
                        .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
                comment.setHidden(true); // hidden 필드가 boolean 타입이라고 가정
                commentRepository.save(comment);
            }
        }
    }
}

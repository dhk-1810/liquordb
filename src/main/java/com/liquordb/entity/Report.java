package com.liquordb.entity;

import com.liquordb.enums.ReportTargetType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportTargetType targetType; // REVIEW 또는 COMMENT

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(nullable = false)
    private UUID requestUserId; // 단순한 정보 표기에 가깝기 떄문에 연관관계 대신 id만 사용

    private String reason;

    private ReportStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime approvedAt; // 승인되면
    private LocalDateTime rejectedAt; // 반려되면

    public enum ReportStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void approve(){
        if (status != ReportStatus.PENDING) return;
        this.status = ReportStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject(){
        if (status != ReportStatus.PENDING) return;
        this.status = ReportStatus.REJECTED;
        this.rejectedAt = LocalDateTime.now();
    }

    @Builder
    private Report(ReportTargetType targetType, Review review, Comment comment, UUID requestUserId, String reason) {
        this.targetType = targetType;
        this.review = review;
        this.comment = comment;
        this.requestUserId = requestUserId;
        this.reason = reason;
    }

    public static Report createReviewReport(Review review, UUID requestUserId, String reason) {
        return Report.builder()
                .targetType(ReportTargetType.REVIEW)
                .review(review)
                .requestUserId(requestUserId)
                .reason(reason)
                .build();
    }

    public static Report createCommentReport(Comment comment, UUID requestUserId, String reason) {
        return Report.builder()
                .targetType(ReportTargetType.COMMENT)
                .comment(comment)
                .requestUserId(requestUserId)
                .reason(reason)
                .build();
    }

}

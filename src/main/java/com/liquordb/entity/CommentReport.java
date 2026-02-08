package com.liquordb.entity;

import com.liquordb.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment_reports")
public class CommentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(nullable = false)
    private UUID requestUserId; // 단순한 정보 표기 역할이기 떄문에 연관관계 대신 id만 사용

    private String reason;

    private ReportStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime approvedAt; // 승인되면
    private LocalDateTime rejectedAt; // 반려되면

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
    private CommentReport(Comment comment, UUID requestUserId, String reason) {
        this.comment = comment;
        this.requestUserId = requestUserId;
        this.reason = reason;
    }

    public static CommentReport create(Comment comment, String reason, UUID requestUserId) {
        return CommentReport.builder()
                .comment(comment)
                .requestUserId(requestUserId)
                .reason(reason)
                .build();
    }

}
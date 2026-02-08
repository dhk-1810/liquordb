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
    private String reason;

    @Column(nullable = false)
    private UUID reporterId; // 단순한 정보 표기 역할이기 떄문에 FK로 사용하진 않음

    @Column(nullable = false)
    private String reporterUsername; // 단순한 정보 표기 역할이기 떄문에 FK로 사용하진 않음

    @Column(nullable = false)
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
    private CommentReport(Comment comment, String reason, UUID reporterId, String reporterUsername) {
        this.comment = comment;
        this.reason = reason;
        this.reporterId = reporterId;
        this.reporterUsername = reporterUsername;
    }

    public static CommentReport create(Comment comment, String reason, UUID reporterId, String reporterUsername) {
        return CommentReport.builder()
                .comment(comment)
                .reason(reason)
                .reporterId(reporterId)
                .reporterUsername(reporterUsername)
                .build();
    }

}
package com.liquordb.entity;

import com.liquordb.enums.ReportTargetType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportTargetType targetType; // REVIEW 또는 COMMENT

    @Column(nullable = false)
    private Long targetId; // // 리뷰 ID 또는 댓글 ID

    // TODO Comment랑 Review를 양방향매핑으로 쓰는게 RDB 차원에서 적절할듯
    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment comment;

    @Column(nullable = false)
    private Long userId; // 신고 넣은 유저

    private String reason;

    private boolean isApproved; // 관리자 검토 후 유효성 판단

    private LocalDateTime createdAt;
    private LocalDateTime approvedAt; // 승인되면
    private LocalDateTime rejectedAt; // 각하되면

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

}

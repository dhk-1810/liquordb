package com.liquordb.review.entity;

import com.liquordb.user.entity.User;
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

    @Column(nullable = false)
    private Long userId; // 신고 넣은 유저

    private String reason;

    private boolean isValid; // 관리자 검토 후 유효성 판단

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

}

package com.liquordb.review.entity;

import com.liquordb.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User reporter;

    private Long targetId;

    private boolean isValid; // 관리자 검토 후 유효성 판단

    @Enumerated(EnumType.STRING)
    private ReportTargetType targetType; // REVIEW or COMMENT

    private String reason;

    private LocalDateTime reportedAt;
}

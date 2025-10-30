package com.liquordb.dto.report;

import com.liquordb.enums.ReportTargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponseDto {
    private Long targetId;
    private ReportTargetType targetType;
    private Long userId;
    private String reason;
    private boolean isApproved;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
}

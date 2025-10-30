package com.liquordb.dto.report;

import com.liquordb.enums.ReportTargetType;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportRequestDto {
    private Long targetId;
    private ReportTargetType targetType;
    private Long userId;
    private String reason;
}

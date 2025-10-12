package com.liquordb.dto.report;

import com.liquordb.entity.ReportTargetType;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDto {
    private Long targetId;
    private ReportTargetType targetType;
    private Long userId;
    private String reason;
}

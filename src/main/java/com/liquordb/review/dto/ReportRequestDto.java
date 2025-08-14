package com.liquordb.review.dto;

import com.liquordb.review.entity.ReportTargetType;
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

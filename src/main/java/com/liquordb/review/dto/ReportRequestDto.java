package com.liquordb.review.dto;

import com.liquordb.review.entity.ReportTargetType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDto {
    private Long targetId;
    private ReportTargetType targetType;
    private String reason;
}

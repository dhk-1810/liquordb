package com.liquordb.dto.report;

import com.liquordb.enums.ReportTargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Builder
public record ReportRequestDto (

        @NotNull(message = "신고 대상의 ID는 필수입니다.")
        Long targetId,

        @NotNull(message = "신고 대상의 종류는 필수입니다. (리뷰 또는 댓글)")
        ReportTargetType targetType,

        @NotBlank(message = "신고 내용을 입력해 주세요.")
        String reason

) {

}

package com.liquordb.dto.report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ReviewReportRequest(

        @NotNull(message = "신고 대상 리뷰 ID는 필수입니다.")
        Long reviewId,

        @NotBlank(message = "신고 내용을 입력해 주세요.")
        String reason
){

}

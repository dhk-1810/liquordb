package com.liquordb.dto.report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
public record CommentReportRequest(

        @NotNull(message = "신고 대상 댓글 ID는 필수입니다.")
        Long commentId,

        @NotBlank(message = "신고 내용을 입력해 주세요.")
        String reason

) {

}

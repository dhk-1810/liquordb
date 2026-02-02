package com.liquordb.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CommentRequestDto (

        @NotNull(message = "리뷰 정보는 필수입니다.")
        Long reviewId,

        Long parentId, // 답글일 경우에만 사용.

        @NotBlank(message = "댓글 내용을 입력해 주세요.")
        String content
){

}

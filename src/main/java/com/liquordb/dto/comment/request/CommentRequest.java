package com.liquordb.dto.comment.request;

import jakarta.validation.constraints.NotBlank;

public record CommentRequest(

        Long parentId, // 답글일 경우에만 사용.

        @NotBlank(message = "댓글 내용을 입력해 주세요.")
        String content
){

}

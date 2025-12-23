package com.liquordb.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.lang.Nullable;

@Builder
public record CommentRequestDto (

        Long parentId, // 답글일 경우에만 사용.

        @NotBlank(message = "댓글 내용을 입력해 주세요.")
        String content
){

}

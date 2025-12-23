package com.liquordb.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CommentUpdateRequestDto (
        @NotBlank(message = "수정할 내용을 입력해 주세요.")
        String content
) {
}

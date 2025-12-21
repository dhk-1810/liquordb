package com.liquordb.dto.comment;

import lombok.Builder;

@Builder
public record CommentUpdateRequestDto (
        String content
) {
}

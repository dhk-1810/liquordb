package com.liquordb.dto.comment;

import lombok.Builder;

@Builder
public record CommentRequestDto (
        Long reviewId,
        Long parentId,
        String content
){

}

package com.liquordb.dto.comment;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequestDto {
    private Long reviewId;
    private Long parentId;
    private String content;
}

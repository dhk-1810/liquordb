package com.liquordb.dto.comment;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    private Long reviewId;
    private Long parentId; // optional
    private String content;
}

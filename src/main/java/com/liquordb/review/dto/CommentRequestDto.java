package com.liquordb.review.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    private Long reviewId;
    private Long parentId; // optional
    private String content;
}

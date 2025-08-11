package com.liquordb.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCommentDto {
    private Long id;
    private Long reviewId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
}

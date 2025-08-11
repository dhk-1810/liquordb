package com.liquordb.review.dto;

import com.liquordb.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSummaryDto {
    private Long id;            // 리뷰 ID
    private String title;       // 리뷰 제목
    private String content;     // 리뷰 내용 (요약)
    private double score;       // 평점
    private String liquorName;  // 주류 이름
    private LocalDateTime createdDate; // 작성일

    public static ReviewSummaryDto from(Review review) {
        return ReviewSummaryDto.builder()
                .id(review.getId())
                .title(review.getTitle())
                .content(review.getContent())
                .score(review.getRating())
                .liquorName(review.getLiquor().getName()) // Liquor 엔티티에서 이름 가져옴
                .createdDate(review.getCreatedAt())
                .build();
    }
}

package com.liquordb.dto.review;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 리뷰 요약 Response DTO입니다
 */
@Builder
public record ReviewSummaryDto (
        Long id,            // 리뷰 ID
        String title,       // 리뷰 제목
        String content,     // 리뷰 내용 (요약)
        double score,       // 평점
        String liquorName,  // 주류 이름
        LocalDateTime createdDate // 작성일
) {


}

package com.liquordb.dto.review;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 리뷰 요약 Response DTO입니다
 */
@Builder
public record ReviewSummaryDto (
        Long id,
        String title,
        String content,     // TODO 리뷰 내용 한줄만 가져와야함.
        double score,
        String liquorName,
        LocalDateTime createdDate
) {


}

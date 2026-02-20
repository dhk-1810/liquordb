package com.liquordb.dto.review;

import com.liquordb.dto.review.reviewdetaildto.ReviewDetailRequest;
import com.liquordb.entity.Liquor;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

/**
 * 리뷰 생성 요청 DTO
 */
public record ReviewRequest(

        @NotNull(message = "평점은 필수입니다.")
        @Min(1) @Max(5)
        Integer rating,

        String title,

        @NotBlank(message = "내용을 입력해주세요.")
        String content,

        @NotNull(message = "주종 카테고리를 선택해주세요.")
        Liquor.LiquorCategory category,

        ReviewDetailRequest reviewDetailRequest

){

}
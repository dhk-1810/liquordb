package com.liquordb.dto.review;

import com.liquordb.dto.review.reviewdetaildto.ReviewDetailRequest;
import com.liquordb.entity.Liquor;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

/**
 * 리뷰 생성 요청 DTO
 */
@Builder
public record ReviewRequestDto (

        @NotNull(message = "리뷰 대상 주류 ID는 필수입니다.")
        Long liquorId,

        @NotNull(message = "평점은 필수입니다.")
        Double rating,

        String title,

        @NotBlank(message = "내용을 입력해주세요.")
        String content,

        Liquor.LiquorCategory category,

        ReviewDetailRequest reviewDetailRequest

){

}
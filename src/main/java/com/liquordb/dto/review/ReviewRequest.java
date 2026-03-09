package com.liquordb.dto.review;

import com.liquordb.dto.review.reviewdetaildto.ReviewDetailRequest;
import com.liquordb.entity.Liquor;
import jakarta.validation.constraints.*;

import java.util.List;

/**
 * 리뷰 생성 요청 DTO
 */
public record ReviewRequest(

        @NotNull(message = "평점은 필수입니다.")
        @Min(1) @Max(10)
        Integer rating,

        String title,

        @NotBlank(message = "내용을 입력해주세요.")
        String content,

        @Size(max = 10, message = "태그는 최대 10개까지 지정 가능합니다.")
        List<String> tags,

        ReviewDetailRequest reviewDetailRequest

){

}
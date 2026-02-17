package com.liquordb.dto.review;

import com.liquordb.dto.review.reviewdetaildto.ReviewDetailRequest;
import com.liquordb.entity.reviewdetail.ReviewDetail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

/**
 * 리뷰 수정 요청 DTO
 */
@Builder
public record ReviewUpdateRequestDto (

        @NotNull(message = "평점은 필수입니다.")
        Short rating,

        String title,

        @NotBlank(message = "내용을 입력해주세요.")
        String content,

        // 주종별 디테일 정보
        ReviewDetailRequest detailRequest,

        // 삭제할 이미지 목록 (File 객체의 ID)
        List<Long> imageIdsToDelete
) {

}

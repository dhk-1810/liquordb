package com.liquordb.review.dto;

import com.liquordb.review.dto.detaildto.ReviewDetailDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequestDto {
    // userId는 JWT 토큰에서 뽑아내 서버에서 설정
    private Long liquorId;

    @NotNull(message = "평점은 필수입니다.")
    private Double rating;

    private String title; // 제목 겸 한줄평. 선택

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
    private ReviewDetailDto detail;
    private List<String> imageUrls; // 선택
}

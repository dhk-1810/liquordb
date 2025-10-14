package com.liquordb.dto.review;

import com.liquordb.dto.review.reviewdetaildto.BeerReviewDetailDto;
import com.liquordb.dto.review.reviewdetaildto.WhiskyReviewDetailDto;
import com.liquordb.dto.review.reviewdetaildto.WineReviewDetailDto;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Getter
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

    // 주종별 디테일 정보
    private BeerReviewDetailDto beerDetail;
    private WineReviewDetailDto wineDetail;
    private WhiskyReviewDetailDto whiskyDetail;

    // 이미지 추가/삭제 리스트
    private List<String> addImageUrls;
    private List<String> removeImageUrls;
}

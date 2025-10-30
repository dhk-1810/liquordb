package com.liquordb.dto.review;

import com.liquordb.dto.review.reviewdetaildto.BeerReviewDetailDto;
import com.liquordb.dto.review.reviewdetaildto.WhiskyReviewDetailDto;
import com.liquordb.dto.review.reviewdetaildto.WineReviewDetailDto;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// 리뷰 생성, 수정 요청 DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
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

    // 이미지 추가
    private List<MultipartFile> images;
}

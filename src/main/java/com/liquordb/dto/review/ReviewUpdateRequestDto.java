package com.liquordb.dto.review;

import com.liquordb.dto.review.reviewdetaildto.BeerReviewDetailDto;
import com.liquordb.dto.review.reviewdetaildto.WhiskyReviewDetailDto;
import com.liquordb.dto.review.reviewdetaildto.WineReviewDetailDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// 리뷰 생성, 수정 요청 DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateRequestDto {
    // userId는 JWT 토큰에서 뽑아내 서버에서 설정

    @NotNull(message = "평점은 필수입니다.")
    private Double rating;

    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    // 주종별 디테일 정보
    private BeerReviewDetailDto beerDetail;
    private WineReviewDetailDto wineDetail;
    private WhiskyReviewDetailDto whiskyDetail;

    // 이미지 삭제 목록
    private List<Long> imageIdsToDelete;

    // 추가할 이미지는 따로 파라미터로 받음.
}

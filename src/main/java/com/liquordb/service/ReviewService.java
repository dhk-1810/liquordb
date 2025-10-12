package com.liquordb.service;

import com.liquordb.entity.Liquor;
import com.liquordb.repository.LiquorRepository;
import com.liquordb.dto.review.ReviewRequestDto;
import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.dto.review.reviewdetaildto.BeerReviewDetailDto;
import com.liquordb.dto.review.reviewdetaildto.WhiskyReviewDetailDto;
import com.liquordb.dto.review.reviewdetaildto.WineReviewDetailDto;
import com.liquordb.entity.Review;
import com.liquordb.entity.ReviewImage;
import com.liquordb.entity.reviewdetail.BeerReviewDetail;
import com.liquordb.entity.reviewdetail.ReviewDetail;
import com.liquordb.entity.reviewdetail.WhiskyReviewDetail;
import com.liquordb.entity.reviewdetail.WineReviewDetail;
import com.liquordb.repository.ReviewDetailRepository;
import com.liquordb.repository.ReviewImageRepository;
import com.liquordb.repository.ReviewRepository;
import com.liquordb.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final LiquorRepository liquorRepository;
    private final ReviewDetailRepository reviewDetailRepository;

    // 리뷰 등록
    @Transactional
    public ReviewResponseDto createReview(User user, ReviewRequestDto dto) {

        Liquor liquor = liquorRepository.findById(dto.getLiquorId())
                .orElseThrow(() -> new RuntimeException("Liquor not found"));

        Review review = Review.builder()
                .user(user)
                .liquor(liquor)
                .rating(dto.getRating())
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();

        reviewRepository.save(review);

        // 주종별 디테일 저장
        ReviewDetail detail = switch (liquor.getCategory()) {
            case BEER -> {
                BeerReviewDetailDto detailDto = dto.getBeerDetail();
                yield (detailDto != null) ? BeerReviewDetail.builder()
                        .review(review)
                        .aroma(detailDto.getAroma())
                        .taste(detailDto.getTaste())
                        .headRetention(detailDto.getHeadRetention())
                        .look(detailDto.getLook())
                        .build() : null;
            }
            case WINE -> {
                WineReviewDetailDto detailDto = dto.getWineDetail();
                yield (detailDto != null) ? WineReviewDetail.builder()
                        .review(review)
                        .sweetness(detailDto.getSweetness())
                        .acidity(detailDto.getAcidity())
                        .body(detailDto.getBody())
                        .tannin(detailDto.getTannin())
                        .build() : null;
            }
            case WHISKY -> {
                WhiskyReviewDetailDto detailDto = dto.getWhiskyDetail();
                yield (detailDto != null) ? WhiskyReviewDetail.builder()
                        .review(review)
                        .aroma(detailDto.getAroma())
                        .taste(detailDto.getTaste())
                        .finish(detailDto.getFinish())
                        .body(detailDto.getBody())
                        .build() : null;
            }
            default -> null;
        };

        if (detail != null) {
            review.setDetail(detail);  // 연관관계 주입
            reviewDetailRepository.save(detail); // 단일 repository
        }

        // 이미지 업로드 (addImageUrls 사용)
        List<String> imagesToAdd = Optional.ofNullable(dto.getAddImageUrls())
                .orElse(Collections.emptyList());

        List<ReviewImage> images = imagesToAdd.stream()
                .map(url -> ReviewImage.builder()
                        .review(review)
                        .imageUrl(url)
                        .build())
                .toList();

        reviewImageRepository.saveAll(images);

        return ReviewResponseDto.builder()
                .id(review.getId())
                .userId(user.getId())
                .liquorId(liquor.getId())
                .rating(review.getRating())
                .title(review.getTitle())
                .content(review.getContent())
                .imageUrls(images.stream().map(ReviewImage::getImageUrl).toList())
                .createdAt(review.getCreatedAt())
                .build();
    }

    // 리뷰 수정
    @Transactional
    public ReviewResponseDto updateReview(Long reviewId, User user, ReviewRequestDto dto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        // 작성자 확인
        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("리뷰를 수정할 권한이 없습니다.");
        }

        // 공통 필드 수정
        review.setRating(dto.getRating());
        review.setTitle(dto.getTitle());
        review.setContent(dto.getContent());

        // 기존 이미지 제거 / 새 이미지 추가
        List<String> imagesToAdd = Optional.ofNullable(dto.getAddImageUrls()).orElse(Collections.emptyList());
        List<String> imagesToRemove = Optional.ofNullable(dto.getRemoveImageUrls()).orElse(Collections.emptyList());

        review.getImages().removeIf(img -> imagesToRemove.contains(img.getImageUrl()));

        imagesToAdd.forEach(url -> review.getImages().add(
                ReviewImage.builder()
                        .review(review)
                        .imageUrl(url)
                        .build()
        ));

        // 주종별 디테일 수정
        ReviewDetail detail = review.getDetail();
        if (detail instanceof BeerReviewDetail beerDetail && dto.getBeerDetail() != null) {
            beerDetail.setAroma(dto.getBeerDetail().getAroma());
            beerDetail.setTaste(dto.getBeerDetail().getTaste());
            beerDetail.setHeadRetention(dto.getBeerDetail().getHeadRetention());
            beerDetail.setLook(dto.getBeerDetail().getLook());
        } else if (detail instanceof WineReviewDetail wineDetail && dto.getWineDetail() != null) {
            wineDetail.setBody(dto.getWineDetail().getBody());
            wineDetail.setSweetness(dto.getWineDetail().getSweetness());
            wineDetail.setAcidity(dto.getWineDetail().getAcidity());
            wineDetail.setTannin(dto.getWineDetail().getTannin());
        } else if (detail instanceof WhiskyReviewDetail whiskyDetail && dto.getWhiskyDetail() != null) {
            whiskyDetail.setAroma(dto.getWhiskyDetail().getAroma());
            whiskyDetail.setTaste(dto.getWhiskyDetail().getTaste());
            whiskyDetail.setFinish(dto.getWhiskyDetail().getFinish());
            whiskyDetail.setBody(dto.getWhiskyDetail().getBody());
        }

        reviewRepository.save(review);

        // DTO로 변환 후 반환
        List<String> imageUrls = review.getImages().stream()
                .map(ReviewImage::getImageUrl)
                .toList();

        return ReviewResponseDto.builder()
                .id(review.getId())
                .userId(user.getId())
                .liquorId(review.getLiquor().getId())
                .rating(review.getRating())
                .title(review.getTitle())
                .content(review.getContent())
                .imageUrls(imageUrls)
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("리뷰를 삭제할 권한이 없습니다.");
        }

        reviewRepository.delete(review);
    }
}

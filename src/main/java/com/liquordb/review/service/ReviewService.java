package com.liquordb.review.service;

import com.liquordb.liquor.entity.Liquor;
import com.liquordb.liquor.repository.LiquorRepository;
import com.liquordb.review.dto.ReviewRequestDto;
import com.liquordb.review.dto.ReviewResponseDto;
import com.liquordb.review.entity.Review;
import com.liquordb.review.entity.ReviewImage;
import com.liquordb.review.repository.ReviewImageRepository;
import com.liquordb.review.repository.ReviewRepository;
import com.liquordb.user.UserValidator;
import com.liquordb.user.entity.User;
import com.liquordb.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final LiquorRepository liquorRepository;
    private final UserValidator userValidator;

    // 리뷰 등록
    @Transactional
    public ReviewResponseDto createReview(User user, ReviewRequestDto dto) {

        Liquor liquor = liquorRepository.findById(dto.getLiquorId())
                .orElseThrow(() -> new RuntimeException("Liquor not found"));

        Review review = Review.builder()
                .user(user)
                .liquor(liquor)
                .rating(dto.getRating())
                .build();

        reviewRepository.save(review);

        List<ReviewImage> images = Optional.ofNullable(dto.getImageUrls())
                .orElse(Collections.emptyList()).stream()
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

        // 작성자 본인만 수정 가능
        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("리뷰를 수정할 권한이 없습니다.");
        }

        // 내용 수정
        review.setRating(dto.getRating());
        review.setTitle(dto.getTitle());
        review.setContent(dto.getContent());

        // 이미지 수정
        reviewImageRepository.deleteAll(review.getImages()); // 기존 이미지 삭제

        List<ReviewImage> newImages = Optional.ofNullable(dto.getImageUrls())
                .orElse(Collections.emptyList()).stream()
                .map(url -> ReviewImage.builder()
                        .review(review)
                        .imageUrl(url)
                        .build())
                .toList();

        reviewImageRepository.saveAll(newImages);

        return ReviewResponseDto.builder()
                .id(review.getId())
                .userId(user.getId())
                .liquorId(review.getLiquor().getId())
                .rating(review.getRating())
                .title(review.getTitle())
                .content(review.getContent())
                .imageUrls(newImages.stream().map(ReviewImage::getImageUrl).toList())
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

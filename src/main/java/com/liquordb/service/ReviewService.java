package com.liquordb.service;

import com.liquordb.PageResponse;
import com.liquordb.ReviewDetailUpdater;
import com.liquordb.entity.*;
import com.liquordb.exception.NotFoundException;
import com.liquordb.mapper.ReviewDetailMapper;
import com.liquordb.mapper.ReviewMapper;
import com.liquordb.repository.*;
import com.liquordb.dto.review.ReviewRequestDto;
import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.dto.review.reviewdetaildto.BeerReviewDetailDto;
import com.liquordb.dto.review.reviewdetaildto.WhiskyReviewDetailDto;
import com.liquordb.dto.review.reviewdetaildto.WineReviewDetailDto;
import com.liquordb.entity.reviewdetail.BeerReviewDetail;
import com.liquordb.entity.reviewdetail.ReviewDetail;
import com.liquordb.entity.reviewdetail.WhiskyReviewDetail;
import com.liquordb.entity.reviewdetail.WineReviewDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final LiquorRepository liquorRepository;
    private final UserRepository userRepository;
    private final ReviewDetailUpdater reviewDetailUpdater;

    // 리뷰 등록
    @Transactional
    public ReviewResponseDto create(User user, ReviewRequestDto dto) {

        Liquor liquor = liquorRepository.findById(dto.getLiquorId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 주류입니다."));
        Review review = Review.builder()
                .user(user)
                .liquor(liquor)
                .rating(dto.getRating())
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();

        // 주종별 디테일 저장
        ReviewDetail detail = ReviewDetailMapper.toEntity(liquor.getCategory(), dto, review);
        review.setDetail(detail);

        // 리뷰이미지 업로드 (addImageUrls 사용)
        List<String> imagesToAdd = Optional.ofNullable(dto.getAddImageUrls())
                .orElse(Collections.emptyList());

        List<ReviewImage> images = imagesToAdd.stream()
                .map(url -> ReviewImage.builder()
                        .review(review)
                        .imageUrl(url)
                        .build())
                .toList();

        reviewImageRepository.saveAll(images);

        return ReviewMapper.toDto(reviewRepository.save(review));
    }

    // 리뷰 단건 조회
    @Transactional(readOnly = true)
    public ReviewResponseDto findById(Long id) {
        return reviewRepository.findById(id)
                .map(ReviewMapper::toDto)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리뷰입니다."));
    }

    // 주류에 따른 리뷰 조회
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> findAllByLiquorIdAndIsHiddenFalse(Pageable pageable, Long liquorId) {
        Liquor liquor = liquorRepository.findById(liquorId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리뷰입니다."));
        return reviewRepository.findAllByLiquorIdAndIsHiddenFalse(pageable, liquorId)
                .map(ReviewMapper::toDto);
    }

    // 유저에 따른 리뷰 조회
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> findAllByUserId(Pageable pageable, UUID userId) {
        User user = userRepository.findByIdAndStatusNot(userId, UserStatus.WITHDRAWN)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));
        return reviewRepository.findAllByUserIdAndIsHiddenFalse(pageable, userId)
                .map(ReviewMapper::toDto);
    }

    // 리뷰 수정
    @Transactional
    public ReviewResponseDto update(Long reviewId, User user, ReviewRequestDto dto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리뷰입니다."));

        // 작성자 확인
        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("리뷰를 수정할 권한이 없습니다.");
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
        reviewDetailUpdater.updateDetail(review.getDetail(), dto);

        return ReviewMapper.toDto(reviewRepository.save(review));
    }

    // 리뷰 삭제
    @Transactional
    public void deleteByIdAndUser(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리뷰입니다."));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("리뷰를 삭제할 권한이 없습니다.");
        }

        reviewRepository.delete(review);
    }

    /**
     * 관리자용
     */

    // 유저ID, 리뷰 조회
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponseDto> findAllByOptionalFilters(UUID userId, Review.ReviewStatus status, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));
        Page<ReviewResponseDto> page = reviewRepository.findAllByOptionalFilters(userId, status, pageable)
                .map(ReviewMapper::toDto);
        return PageResponse.from(page);
    }
}

package com.liquordb.service;

import com.liquordb.PageResponse;
import com.liquordb.ReviewDetailUpdater;
import com.liquordb.dto.review.ReviewUpdateRequestDto;
import com.liquordb.entity.*;
import com.liquordb.enums.UserStatus;
import com.liquordb.exception.LiquorNotFoundException;
import com.liquordb.exception.ReviewNotFoundException;
import com.liquordb.exception.user.UnauthorizedUserException;
import com.liquordb.exception.user.UserNotFoundException;
import com.liquordb.mapper.ReviewDetailMapper;
import com.liquordb.mapper.ReviewMapper;
import com.liquordb.repository.*;
import com.liquordb.dto.review.ReviewRequestDto;
import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.entity.reviewdetail.ReviewDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final LiquorRepository liquorRepository;
    private final UserRepository userRepository;
    private final ReviewDetailUpdater reviewDetailUpdater;
    private final FileService fileService;
    private final CommentRepository commentRepository;

    // 리뷰 등록
    @Transactional
    public ReviewResponseDto create(User user, ReviewRequestDto dto, List<MultipartFile> images) {

        Liquor liquor = liquorRepository.findById(dto.getLiquorId())
                .orElseThrow(() -> new LiquorNotFoundException(dto.getLiquorId()));
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

        // 리뷰이미지 업로드 및 저장
        images.forEach(file -> {
            review.getImages().add(fileService.upload(file));
        });

        return ReviewMapper.toDto(reviewRepository.save(review));
    }

    // 리뷰 단건 조회
    @Transactional(readOnly = true)
    public ReviewResponseDto findById(Long id) {
        return reviewRepository.findById(id)
                .map(ReviewMapper::toDto)
                .orElseThrow(() -> new ReviewNotFoundException(id));
    }

    // 주류에 따른 리뷰 조회
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> findAllByLiquorIdAndIsHiddenFalse(Pageable pageable, Long liquorId) {
        Liquor liquor = liquorRepository.findById(liquorId)
                .orElseThrow(() -> new LiquorNotFoundException(liquorId));
        return reviewRepository.findAllByLiquorIdAndIsHiddenFalse(pageable, liquorId)
                .map(ReviewMapper::toDto);
    }

    // 유저에 따른 리뷰 조회
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> findAllByUserId(Pageable pageable, UUID userId) {
        User user = userRepository.findByIdAndStatusNot(userId, UserStatus.WITHDRAWN)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return reviewRepository.findAllByUserIdAndIsHiddenFalse(pageable, userId)
                .map(ReviewMapper::toDto);
    }

    // 리뷰 수정
    @Transactional
    public ReviewResponseDto update(Long reviewId, User user, ReviewUpdateRequestDto dto, List<MultipartFile> newImages) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        // 작성자 확인
        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("리뷰를 수정할 권한이 없습니다.");
        }

        // 공통 필드 수정
        review.setRating(dto.getRating());
        review.setTitle(dto.getTitle());
        review.setContent(dto.getContent());

        removeImage(review, dto.getImageIdsToDelete());
        addImage(review, newImages);

        // 주종별 디테일 수정
        // TODO reviewDetailUpdater.updateDetail(review.getDetail(), dto);

        return ReviewMapper.toDto(reviewRepository.save(review));
    }

    public void addImage(Review review, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) return;
        images.forEach(image -> {
            review.getImages().add(fileService.upload(image));
        });
    }

    public void removeImage(Review review, List<Long> imageIdsToDelete) {
        if (imageIdsToDelete == null || imageIdsToDelete.isEmpty()) return;

        review.getImages().removeIf(image -> {
            if (imageIdsToDelete.contains(image.getFilePath())) {
                fileService.delete(image.getId()); // 실제 파일 삭제
                return true;
            }
            return false;
        });
    }

    public void updateBeerReview(){

    }

    // 리뷰 삭제 (Soft Delete)
    @Transactional
    public void deleteByIdAndUser(Long reviewId, User requestUser) {
        Review review = reviewRepository.findByIdAndStatus_Active(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        UUID requestUserId = requestUser.getId();
        if (!review.getUser().getId().equals(requestUser.getId())) {
            throw new UnauthorizedUserException(requestUserId);
        }
        LocalDateTime reviewDeletedAt = LocalDateTime.now().withNano(0);
        commentRepository.softDeleteCommentsByReview(review, reviewDeletedAt);
        review.setStatus(Review.ReviewStatus.DELETED);
        review.setDeletedAt(reviewDeletedAt);
        reviewRepository.save(review);
    }

    /**
     * 관리자용
     */

    // 유저ID, 리뷰 조회
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponseDto> findAllByOptionalFilters(UUID userId, Review.ReviewStatus status, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Page<ReviewResponseDto> page = reviewRepository.findAllByOptionalFilters(userId, status, pageable)
                .map(ReviewMapper::toDto);
        return PageResponse.from(page);
    }
}

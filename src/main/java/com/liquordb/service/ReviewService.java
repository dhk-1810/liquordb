package com.liquordb.service;

import com.liquordb.dto.CursorPageResponse;
import com.liquordb.dto.PageResponse;
import com.liquordb.ReviewDetailUpdater;
import com.liquordb.dto.review.ReviewListGetRequest;
import com.liquordb.dto.review.ReviewUpdateRequestDto;
import com.liquordb.entity.*;
import com.liquordb.exception.liquor.LiquorNotFoundException;
import com.liquordb.exception.review.ReviewAccessDeniedException;
import com.liquordb.exception.review.ReviewNotFoundException;
import com.liquordb.exception.user.UserNotFoundException;
import com.liquordb.mapper.ReviewMapper;
import com.liquordb.repository.*;
import com.liquordb.dto.review.ReviewRequestDto;
import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.repository.comment.CommentRepository;
import com.liquordb.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final UserRepository userRepository;
    private final LiquorRepository liquorRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final ReviewDetailUpdater reviewDetailUpdater;
    private final FileService fileService;

    // 리뷰 등록
    @Transactional
    public ReviewResponseDto create(Long liquorId, ReviewRequestDto request, List<MultipartFile> images, UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Liquor liquor = liquorRepository.findById(liquorId)
                .orElseThrow(() -> new LiquorNotFoundException(liquorId));

        Review review = ReviewMapper.toEntity(request, liquor, user);

        images.forEach(file ->
            review.getImages().add(fileService.upload(file))
        );

        reviewRepository.save(review);
        return ReviewMapper.toDto(review);
    }

    // 리뷰 단건 조회
    @Transactional(readOnly = true)
    public ReviewResponseDto get(Long id) {
        return reviewRepository.findById(id)
                .map(ReviewMapper::toDto)
                .orElseThrow(() -> new ReviewNotFoundException(id));
    }

    // 주류별 리뷰 목록 조회
    @Transactional(readOnly = true)
    public CursorPageResponse<ReviewResponseDto> getAllByLiquor(Long liquorId, ReviewListGetRequest request) {

        liquorRepository.findByIdAndIsDeleted(liquorId, false)
                .orElseThrow(() -> new LiquorNotFoundException(liquorId));

        Slice<Review> reviews = reviewRepository.findByLiquorId(liquorId, Review.ReviewStatus.ACTIVE, request);
        return getCursorPageResponse(reviews);
    }

    // 유저별 리뷰 목록 조회
    @Transactional(readOnly = true)
    public CursorPageResponse<ReviewResponseDto> getAllByUser(UUID authorId, ReviewListGetRequest request) {

        userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException(authorId));

        Slice<Review> reviews = reviewRepository.findByUserId(authorId, Review.ReviewStatus.ACTIVE, request);
        return getCursorPageResponse(reviews);
    }

    // 리뷰 수정
    @Transactional
    public ReviewResponseDto update(Long reviewId, ReviewUpdateRequestDto request, List<MultipartFile> newImages, UUID userId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        if (!review.getUser().getId().equals(userId)) {
            throw new ReviewAccessDeniedException(reviewId, userId);
        }

        // 공통 필드 수정
        review.update(request);

        removeImage(review, request.imageIdsToDelete());
        addImage(review, newImages);

        // 주종별 디테일 수정
        reviewDetailUpdater.updateDetail(review.getDetail(), request.detailRequest());

        return ReviewMapper.toDto(reviewRepository.save(review));
    }

    // 리뷰 삭제 (Soft Delete)
    @Transactional
    public void delete(Long reviewId, UUID userId) {
        Review review = reviewRepository.findByIdAndStatusNot(reviewId, Review.ReviewStatus.DELETED)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
        if (!review.getUser().getId().equals(userId)) {
            throw new ReviewAccessDeniedException(reviewId, userId);
        }

        LocalDateTime reviewDeletedAt = LocalDateTime.now().withNano(0);
        commentRepository.softDeleteCommentsByReview(review, reviewDeletedAt);
        review.softDelete(reviewDeletedAt);
        reviewRepository.save(review);
    }

    // 페이지네이션 헬퍼 메서드
    private CursorPageResponse<ReviewResponseDto> getCursorPageResponse(Slice<Review> reviews) {
        Slice<ReviewResponseDto> response = reviews.map(ReviewMapper::toDto);

        Long nextCursor = null;
        if (response.hasNext()) {
            List<ReviewResponseDto> content = response.getContent();
            nextCursor = content.get(content.size() - 1).id();
        }

        return CursorPageResponse.from(response, nextCursor);
    }

    public void addImage(Review review, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) return;
        images.forEach(image ->
                review.getImages().add(fileService.upload(image))
        );
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

    /**
     * 관리자용
     */

    // 전체 리뷰 조회
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponseDto> findAllByOptionalFilters(UUID userId, Review.ReviewStatus status, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Page<ReviewResponseDto> page = reviewRepository.findAll(userId, status, pageable)
                .map(ReviewMapper::toDto);
        return PageResponse.from(page);
    }
}

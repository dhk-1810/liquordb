package com.liquordb.service;

import com.liquordb.dto.CursorPageResponse;
import com.liquordb.dto.FileResponseDto;
import com.liquordb.dto.PageResponse;
import com.liquordb.ReviewDetailUpdater;
import com.liquordb.dto.review.*;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.*;
import com.liquordb.enums.SortReviewBy;
import com.liquordb.enums.SortDirection;
import com.liquordb.enums.UserStatus;
import com.liquordb.exception.liquor.LiquorNotFoundException;
import com.liquordb.exception.review.ReviewAccessDeniedException;
import com.liquordb.exception.review.ReviewNotFoundException;
import com.liquordb.exception.user.UserNotFoundException;
import com.liquordb.mapper.ReviewMapper;
import com.liquordb.mapper.TagMapper;
import com.liquordb.repository.LiquorTagRepository;
import com.liquordb.repository.ReviewTagRepository;
import com.liquordb.repository.comment.CommentRepository;
import com.liquordb.repository.liquor.LiquorRepository;
import com.liquordb.repository.review.ReviewImageKeyRepository;
import com.liquordb.repository.review.ReviewRepository;
import com.liquordb.repository.review.condition.ReviewListGetCondition;
import com.liquordb.repository.review.condition.ReviewSearchCondition;
import com.liquordb.repository.tag.TagRepository;
import com.liquordb.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ReviewService {

    private final UserRepository userRepository;
    private final LiquorRepository liquorRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageKeyRepository reviewImageKeyRepository;
    private final CommentRepository commentRepository;
    private final TagRepository tagRepository;
    private final LiquorTagRepository liquorTagRepository;
    private final ReviewDetailUpdater reviewDetailUpdater;
    private final FileService fileService; // 단방향 참조
    private final S3Service s3Service; // 단방향 참조
    private final ReviewTagRepository reviewTagRepository;

    // 리뷰 등록
    @Transactional
    public ReviewResponseDto create(Long liquorId, ReviewRequest request, List<MultipartFile> images, UUID userId) {

        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Liquor liquor = liquorRepository.findById(liquorId)
                .orElseThrow(() -> new LiquorNotFoundException(liquorId));

        Review review = ReviewMapper.toEntity(request, liquor, user);
        reviewRepository.save(review);
        liquor.updateAverageRating(request.rating());

        // 태그 추가, 없으면 새로 생성
        Set<TagResponseDto> tagDtos = new HashSet<>();
        if (request.tags() != null && !request.tags().isEmpty()) {
            Set<Tag> tags = request.tags().stream()
                    .map(name -> tagRepository.findByName(name)
                            .orElseGet(() -> tagRepository.save(Tag.create(name))))
                    .collect(Collectors.toSet());
            Set<LiquorTag> liquorTags = new HashSet<>();
            Set<ReviewTag> reviewTags = new HashSet<>();
            tags.forEach(tag -> {
                LiquorTag liquorTag = LiquorTag.create(liquor, tag);
                ReviewTag reviewTag = ReviewTag.create(review, tag);
                liquorTags.add(liquorTag);
                reviewTags.add(reviewTag);
            });
            liquorTagRepository.saveAll(liquorTags);
            reviewTagRepository.saveAll(reviewTags);
            tagDtos = tags.stream()
                    .map(TagMapper::toDto)
                    .collect(Collectors.toSet());
        }

        List<String> imageUrls = new ArrayList<>();
        List<ReviewImageKey> keys = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            images.forEach(file -> {
                        FileResponseDto dto = fileService.upload(file, File.FileType.REVIEW, review.getId());
                        keys.add(new ReviewImageKey(review, dto.key()));
                        imageUrls.add(s3Service.getReviewImageUrl(dto.key()));
                    }
            );
            reviewImageKeyRepository.saveAll(keys);
        }
        return ReviewMapper.toDto(review, tagDtos, imageUrls);
    }

    // 리뷰 단건 조회
    @Transactional(readOnly = true)
    public ReviewResponseDto get(Long id) {

        Review review = reviewRepository.findByIdAndStatusWithTags(id, Review.ReviewStatus.ACTIVE)
                .orElseThrow(() -> new ReviewNotFoundException(id));

        Set<TagResponseDto> tags = review.getReviewTags().stream()
                .map(TagMapper::toDto)
                .collect(Collectors.toSet());

        // default_batch_fetch_size로 N+1 방지
        List<String> imageUrls = getImageUrls(review);
        return ReviewMapper.toDto(review, tags, imageUrls);
    }

    // 주류별 리뷰 목록 조회
    @Transactional(readOnly = true)
    public CursorPageResponse<ReviewResponseDto> getAllByLiquorId(Long liquorId, ReviewListGetRequest request) {

        liquorRepository.findByIdAndIsDeleted(liquorId, false)
                .orElseThrow(() -> new LiquorNotFoundException(liquorId));

        int limit = request.limit() == null ? 20 : request.limit();
        SortReviewBy sortBy = request.sortBy() == null ? SortReviewBy.REVIEW_ID : request.sortBy();
        SortDirection sortDirection = request.sortDirection() == null ? SortDirection.DESC : request.sortDirection();

        ReviewListGetCondition condition = ReviewListGetCondition.builder()
                .liquorId(liquorId)
                .status(Review.ReviewStatus.ACTIVE)
                .cursor(request.cursor())
                .idAfter(request.idAfter())
                .limit(limit)
                .sortBy(sortBy)
                .descending(sortDirection == SortDirection.DESC)
                .build();

        Slice<Review> reviews = reviewRepository.findByLiquorIdWithTags(condition);
        return getCursorPageResponse(reviews);
    }

    // 유저별 리뷰 목록 조회
    @Transactional(readOnly = true)
    public CursorPageResponse<ReviewResponseDto> getAllByUserId(UUID authorId, ReviewListGetRequest request) {

        int limit = request.limit() == null ? 20 : request.limit();
        SortReviewBy sortBy = request.sortBy() == null ? SortReviewBy.REVIEW_ID : request.sortBy();
        SortDirection sortDirection = request.sortDirection() == null ? SortDirection.DESC : request.sortDirection();

        ReviewListGetCondition condition = ReviewListGetCondition.builder()
                .userId(authorId)
                .status(Review.ReviewStatus.ACTIVE)
                .rating(request.rating())
                .cursor(request.cursor())
                .idAfter(request.idAfter())
                .limit(limit)
                .sortBy(sortBy)
                .descending(sortDirection == SortDirection.DESC)
                .build();

        Slice<Review> reviews = reviewRepository.findByUserIdWithTags(condition);
        return getCursorPageResponse(reviews);
    }

    // 리뷰 수정
    @Transactional
    public ReviewResponseDto update(Long reviewId, ReviewUpdateRequest request, UUID userId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        if (!review.getUser().getId().equals(userId)) {
            throw new ReviewAccessDeniedException(reviewId, userId);
        }

        // 공통 필드 수정
        review.update(request);

        // 주종별 디테일 수정
        reviewDetailUpdater.updateDetail(review.getDetail(), request.detailRequest());
        reviewRepository.save(review);

        List<String> imageUrls = getImageUrls(review);
        Set<TagResponseDto> tags = getTags(reviewId);
        return ReviewMapper.toDto(review, tags, imageUrls);
    }

    // 리뷰 삭제 (Soft Delete)
    @Transactional
    public void delete(Long reviewId, UUID userId) {

        // 삭제되지 않은 리뷰와 주류 정보를 FETCH JOIN 조회.
        Review review = reviewRepository.findByIdWithLiquorAndStatusNot(reviewId, Review.ReviewStatus.DELETED)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        if (!review.getUser().getId().equals(userId)) {
            throw new ReviewAccessDeniedException(reviewId, userId);
        }

        Liquor liquor = review.getLiquor();
        liquor.removeReviewRating(review.getRating());

        LocalDateTime reviewDeletedAt = LocalDateTime.now().withNano(0);
        commentRepository.softDeleteCommentsByReview(review, reviewDeletedAt, Comment.CommentStatus.DELETED);

        review.softDelete(reviewDeletedAt);
        reviewRepository.save(review);
    }

    private Set<TagResponseDto> getTags(Long id) {
        return reviewTagRepository.findByReview_Id(id)
                .stream().map(TagMapper::toDto)
                .collect(Collectors.toSet());
    }

    private List<String> getImageUrls(Review review) {
        // default_batch_fetch_size: 100 옵션으로 N+1 문제 방지
        return review.getImageKeys().stream()
                .map(reviewImageKey-> s3Service.getReviewImageUrl(reviewImageKey.getId().toString()))
                .toList();
    }

    // 페이지네이션 헬퍼 메서드
    private CursorPageResponse<ReviewResponseDto> getCursorPageResponse(Slice<Review> reviews) {
        Slice<ReviewResponseDto> response = reviews
                .map(r -> ReviewMapper.toDto(r, getTags(r.getId()), getImageUrls(r)));

        Long nextCursor = null;
        if (response.hasNext()) {
            List<ReviewResponseDto> content = response.getContent();
            nextCursor = content.get(content.size() - 1).id();
        }

        return CursorPageResponse.from(response, nextCursor);
    }

    /**
     * 관리자용
     */

    // 전체 리뷰 조회
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponseDto> getAll(ReviewSearchRequest request) {
        ReviewSearchCondition condition = getSearchCondition(request);
        Page<ReviewResponseDto> page = reviewRepository.findAllWithTags(condition)
                .map(r -> ReviewMapper.toDto(r, getTags(r.getId()), getImageUrls(r)));
        return PageResponse.from(page);
    }

    private ReviewSearchCondition getSearchCondition(ReviewSearchRequest request) {
        Review.ReviewStatus status = request.status() == null
                ? Review.ReviewStatus.ACTIVE
                : request.status();
        int page = request.page() == null
                ? 0
                : request.page();
        int limit = request.limit() == null
                ? 20
                : request.limit();
        boolean descending = request.sortDirection() == SortDirection.DESC;
        return new ReviewSearchCondition(request.username(), status, page, limit, descending);
    }
}

package com.liquordb.service;

import com.liquordb.dto.CursorPageResponse;
import com.liquordb.dto.liquor.*;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.enums.SortLiquorBy;
import com.liquordb.enums.SortDirection;
import com.liquordb.exception.liquor.LiquorNotFoundException;
import com.liquordb.mapper.LiquorMapper;
import com.liquordb.entity.Liquor;
import com.liquordb.mapper.TagMapper;
import com.liquordb.repository.*;
import com.liquordb.repository.comment.CommentRepository;
import com.liquordb.repository.liquor.LiquorRepository;
import com.liquordb.repository.liquor.condition.LiquorSearchCondition;
import com.liquordb.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LiquorService {

    private final LiquorRepository liquorRepository;
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final LiquorLikeRepository liquorLikeRepository;
    private final LiquorTagRepository liquorTagRepository;

    // 주류 목록 조회 (전체 조회 또는 검색어, 대분류, 소분류별로 필터링)
    @Transactional(readOnly = true)
    public CursorPageResponse<LiquorSummaryDto> getAll(LiquorListGetRequest request, UUID userId) {

        int limit = request.limit() == null ? 50 : request.limit();
        SortLiquorBy sortBy = request.sortBy() == null ? SortLiquorBy.LIQUOR_ID : request.sortBy();
        SortDirection sortDirection = request.sortDirection() == null ? SortDirection.DESC : request.sortDirection();

        LiquorSearchCondition condition = LiquorSearchCondition.builder()
                .category(request.category())
                .subcategory(request.subcategory())
                .keyword(request.keyword())
                .searchDeleted(request.searchDeleted())
                .cursor(request.cursor())
                .idAfter(request.idAfter())
                .limit(limit)
                .sortBy(sortBy)
                .descending(sortDirection == SortDirection.DESC)
                .build();
        Slice<Liquor> liquors = liquorRepository.findAll(condition);

        List<Long> liquorIds = liquors.getContent().stream()
                .map(Liquor::getId)
                .toList();
        Set<Long> likedLiquorIds = (userId != null)
                ? liquorLikeRepository.findLikedLiquorIdsByUserIdAndLiquorIds(userId, liquorIds)
                : Collections.emptySet();

        Slice<LiquorSummaryDto> response = liquors.map(liquor -> {
            boolean isLiked = likedLiquorIds.contains(liquor.getId());
            return LiquorMapper.toSummaryDto(liquor, isLiked, liquor.getReviewCount(), liquor.getLikeCount());
        });

        Object nextCursor = null;
        if (response.hasNext()) {
            List<LiquorSummaryDto> content = response.getContent();
            nextCursor = content.get(content.size() - 1).id();
        }

        return CursorPageResponse.from(response, nextCursor);
    }

    // 주류 단건 조회
    @Transactional(readOnly = true)
    public LiquorResponseDto getLiquorDetail(Long liquorId, UUID userId) {

        Liquor liquor = liquorRepository.findByIdAndIsDeleted(liquorId, false)
                .orElseThrow(() -> new LiquorNotFoundException(liquorId));

        Set<TagResponseDto> tags = liquorTagRepository.findAllByLiquor_Id(liquorId).stream()
                .map(liquorTag -> TagMapper.toDto(liquorTag.getTag()))
                .collect(Collectors.toSet());

        boolean likedByMe = (userId != null) && liquorLikeRepository.existsByLiquor_IdAndUser_Id(liquorId, userId);

        return LiquorMapper.toDto(liquor, tags, likedByMe);
    }

    /**
     * 관리자용
     */

    // 주류 등록
    @Transactional
    public LiquorResponseDto create(LiquorRequest request) {
        Liquor liquor = LiquorMapper.toEntity(request);
        liquorRepository.save(liquor);
        return LiquorMapper.toDto(liquor, null, false);
    }

    // 주류 수정
    @Transactional
    public LiquorResponseDto update(Long id, LiquorUpdateRequest request) {
        Liquor liquor = liquorRepository.findById(id)
                .orElseThrow(() -> new LiquorNotFoundException(id));
        liquor.updateFromDto(request.isDiscontinued(), request.deleteImage());
        liquorRepository.save(liquor);
        return LiquorMapper.toDto(liquor, null, false);
    }

    // 주류 삭제 (Soft Delete)
    @Transactional
    public void deleteById(Long id) {
        Liquor liquor = liquorRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new LiquorNotFoundException(id));

        LocalDateTime liquorDeletedAt = LocalDateTime.now().withNano(0);
        commentRepository.softDeleteCommentsByLiquor(liquor, liquorDeletedAt);
        reviewRepository.softDeleteReviewsByLiquor(liquor, liquorDeletedAt);
        liquor.softDelete(liquorDeletedAt);

        liquorRepository.save(liquor);
    }

}
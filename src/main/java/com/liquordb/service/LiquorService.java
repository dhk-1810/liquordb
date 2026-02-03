package com.liquordb.service;

import com.liquordb.PageResponse;
import com.liquordb.dto.liquor.LiquorUpdateRequestDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.LiquorLike;
import com.liquordb.entity.User;
import com.liquordb.exception.liquor.LiquorNotFoundException;
import com.liquordb.mapper.LiquorMapper;
import com.liquordb.dto.liquor.LiquorRequestDto;
import com.liquordb.dto.liquor.LiquorResponseDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.entity.Liquor;
import com.liquordb.entity.LiquorSubcategory;
import com.liquordb.mapper.TagMapper;
import com.liquordb.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // 주류 목록 조회 (전체 조회 또는 대분류, 소분류별로 필터링)
    @Transactional(readOnly = true)
    public PageResponse<LiquorSummaryDto> getLiquorsByFilters(
            Liquor.LiquorCategory category,
            LiquorSubcategory subcategory,
            UUID userId,  // 조회하는 사람, null 허용.
            Pageable pageable
    ) {
        Page<Liquor> liquors = fetchLiquors(category, subcategory, pageable);
        List<Long> liquorIds = liquors.getContent().stream()
                .map(Liquor::getId)
                .toList();
        Set<Long> likedLiquorIds = (userId != null)
                ? liquorLikeRepository.findLikedLiquorIdsByUserIdAndLiquorIds(userId, liquorIds)
                : Collections.emptySet();

        Page<LiquorSummaryDto> response = liquors.map(liquor -> {
            boolean isLiked = likedLiquorIds.contains(liquor.getId());
            return LiquorMapper.toSummaryDto(liquor, isLiked, liquor.getReviewCount(), liquor.getLikeCount());
        });

        return PageResponse.from(response);
    }

    // 주류 목록 검색 (이름으로)
    @Transactional(readOnly = true)
    public PageResponse<LiquorSummaryDto> searchLiquorsByName(String keyword, UUID userId, Pageable pageable) {

        Page<Liquor> searchedLiquors = liquorRepository.findByNameContainingAndIsDeleted(pageable, keyword, false);
        List<Long> liquorIds = searchedLiquors.getContent().stream()
                .map(Liquor::getId)
                .toList();
        Set<Long> likedLiquorIds = (userId != null)
                ? liquorLikeRepository.findLikedLiquorIdsByUserIdAndLiquorIds(userId, liquorIds)
                : Collections.emptySet();
        Page<LiquorSummaryDto> response = searchedLiquors.map(liquor -> {
            boolean isLiked = likedLiquorIds.contains(liquor.getId());
            return LiquorMapper.toSummaryDto(liquor, isLiked, liquor.getReviewCount(), liquor.getLikeCount());
        });
        return PageResponse.from(response);
    }

    // 주류 상세 페이지
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

    private Page<Liquor> fetchLiquors(
            Liquor.LiquorCategory category,
            LiquorSubcategory subcategory,
            Pageable pageable
    ) {
        if (subcategory != null) {
            return liquorRepository.findBySubcategoryAndIsDeleted(pageable, subcategory, false);
        } else if (category != null) {
            return liquorRepository.findByCategoryAndIsDeleted(pageable, category, false);
        } else {
            return liquorRepository.findAllByIsDeleted(pageable, false);
        }
    }

    /**
     * 이하는 관리자용 메서드들입니다.
     */

    // 주류 등록
    @Transactional
    public LiquorResponseDto create(LiquorRequestDto request) {
        Liquor liquor = LiquorMapper.toEntity(request);
        liquorRepository.save(liquor);
        return LiquorMapper.toDto(liquor, null, false);
    }

    // 주류 수정
    @Transactional
    public LiquorResponseDto update(Long id, LiquorUpdateRequestDto request) {
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
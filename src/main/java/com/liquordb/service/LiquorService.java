package com.liquordb.service;

import com.liquordb.PageResponse;
import com.liquordb.dto.liquor.LiquorUpdateRequestDto;
import com.liquordb.entity.LiquorLike;
import com.liquordb.entity.User;
import com.liquordb.exception.liquor.LiquorNotFoundException;
import com.liquordb.mapper.LiquorMapper;
import com.liquordb.dto.liquor.LiquorRequestDto;
import com.liquordb.dto.liquor.LiquorResponseDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.entity.Liquor;
import com.liquordb.entity.LiquorSubcategory;
import com.liquordb.repository.CommentRepository;
import com.liquordb.repository.LiquorLikeRepository;
import com.liquordb.repository.LiquorRepository;
import com.liquordb.repository.ReviewRepository;
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

@Service
@RequiredArgsConstructor
public class LiquorService {

    private final LiquorRepository liquorRepository;
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final LiquorLikeRepository liquorLikeRepository;

    // 주류 목록 조회 (전체 조회 또는 대분류, 소분류별로 필터링)
    @Transactional(readOnly = true)
    public PageResponse<LiquorSummaryDto> getLiquorsByFilters(Liquor.LiquorCategory category,
                                                              LiquorSubcategory subcategory,
                                                              Pageable pageable,
                                                              UUID userId) { // 조회하는 사람, null 허용.

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
    public PageResponse<LiquorSummaryDto> searchLiquorsByName(String keyword, Pageable pageable, UUID userId) {
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
    public LiquorResponseDto getLiquorDetail(Long liquorId, User user) {

        // 주류 정보 조회 (삭제되지 않은 것만)
        Liquor liquor = liquorRepository.findByIdAndIsDeleted(liquorId, false)
                .orElseThrow(() -> new LiquorNotFoundException(liquorId));

        return LiquorMapper.toDto(liquor, user);
    }

    /**
     * 이하는 관리자용 메서드들입니다.
     */

    // 주류 등록
    @Transactional
    public LiquorResponseDto create(LiquorRequestDto request) {
        Liquor liquor = LiquorMapper.toEntity(request);
        return LiquorMapper.toDto(liquorRepository.save(liquor), null);
    }

    // 주류 수정
    @Transactional
    public LiquorResponseDto update(Long id, LiquorUpdateRequestDto request) {
        Liquor liquor = liquorRepository.findById(id)
                .orElseThrow(() -> new LiquorNotFoundException(id));
        liquor.updateFromDto(request.isDiscontinued(), request.deleteImage());
        return LiquorMapper.toDto(liquorRepository.save(liquor), null);
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

    private Page<Liquor> fetchLiquors(Liquor.LiquorCategory category,
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
}
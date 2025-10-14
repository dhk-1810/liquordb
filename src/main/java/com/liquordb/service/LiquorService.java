package com.liquordb.service;

import com.liquordb.repository.LiquorLikeRepository;
import com.liquordb.dto.liquor.LiquorRequestDto;
import com.liquordb.dto.liquor.LiquorResponseDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.entity.Liquor;
import com.liquordb.entity.LiquorSubcategory;
import com.liquordb.entity.LiquorCategory;
import com.liquordb.repository.LiquorRepository;
import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LiquorService {

    private final LiquorLikeRepository liquorLikeRepository;
    private final LiquorRepository liquorRepository;
    private final ReviewRepository reviewRepository;
    private final TagService tagService;

    // 1. 주류 조회 (전체 조회 또는 대분류, 소분류별로 필터링)
    public List<LiquorSummaryDto> getLiquorsByFilters(LiquorCategory category, LiquorSubcategory subcategory) {
        List<LiquorSummaryDto> liquors;

        if (category == null && subcategory == null) { // 전체 주류 조회
            return liquorRepository.findAllWithCategoryAndCounts();
        } else if (category != null && subcategory == null) {
            return liquorRepository.findSummaryByCategory(category); // 대분류로 필터링
        } else /* (subcategory != null) */ {
            return liquorRepository.findSummaryBySubcategory(subcategory); // 소분류로 필터링
        }
    }

    // 2. 주류 검색 (이름으로)
    public List<LiquorSummaryDto> searchLiquorsByName(String keyword) {
        return liquorRepository.findSummaryByNameContaining(keyword);
    }

    // 3. 주류 상세 페이지
    @Transactional(readOnly = true)
    public LiquorResponseDto getLiquorDetail(Long liquorId, UUID currentUserId) {

        // 주류 정보 조회 (삭제되지 않은 것만)
        Liquor liquor = liquorRepository.findByIdAndIsHiddenFalse(liquorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주류를 찾을 수 없습니다."));

        // 리뷰 평균 점수 및 개수
        double avgRating = reviewRepository.getAverageRatingByLiquorId(liquorId);
        int reviewCount = reviewRepository.countByLiquorId(liquorId);
        List<ReviewResponseDto> reviews = reviewRepository.findAllByLiquorId(liquorId).stream()
                .map(ReviewResponseDto::from)
                .toList();

        // 좋아요 개수
        long likeCount = liquorLikeRepository.countByLiquorId(liquorId);

        // 유저별 좋아요 여부
        boolean likedByMe = liquorLikeRepository.existsByUserIdAndLiquorId(currentUserId, liquorId);

        // 태그 정보
        List<String> tags = tagService.getTagsForLiquor(liquorId);

        return LiquorResponseDto.builder()
                .id(liquor.getId())
                .name(liquor.getName())
                .category(liquor.getCategory())
                .subcategory(liquor.getSubcategory())
                .country(liquor.getCountry())
                .manufacturer(liquor.getManufacturer())
                .abv(liquor.getAbv())
                .isDiscontinued(liquor.isDiscontinued())
                .imageUrl(liquor.getImageUrl())
                .averageRating(avgRating)
                .reviewCount(reviewCount)
                .reviews(reviews)
                .likeCount(likeCount)
                .likedByMe(likedByMe)
                .tags(tags)
                .build();
    }


    /**
     * 이하는 관리자용 메서드들입니다.
     */

    // 주류 등록
    public void createLiquor(LiquorRequestDto dto) {
        Liquor liquor = Liquor.builder()
                .name(dto.getName())
                .category(dto.getCategory())
                .subcategory(dto.getSubcategory())
                .country(dto.getCountry())
                .manufacturer(dto.getManufacturer())
                .abv(dto.getAbv())
                .imageUrl(dto.getImageUrl())
                .build();
        liquorRepository.save(liquor);
    }

    // 주류 수정
    public void updateLiquor(Long id, LiquorRequestDto dto) {
        Liquor liquor = liquorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Liquor not found"));
        liquor.updateFromDto(dto);
    }

    // 주류 숨기기
    public void toggleHidden(Long id) {
        Liquor liquor = liquorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Liquor not found"));
        liquor.setHidden(!liquor.isHidden());
    }

    // 주류 삭제
    public void deleteLiquor(Long id) {
        liquorRepository.deleteById(id);
    }

}

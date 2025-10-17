package com.liquordb.service;

import com.liquordb.dto.liquor.LiquorTagResponseDto;
import com.liquordb.entity.User;
import com.liquordb.exception.NotFoundException;
import com.liquordb.mapper.LiquorMapper;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LiquorService {

    private final LiquorRepository liquorRepository;

    // 1. 주류 목록 조회 (전체 조회 또는 대분류, 소분류별로 필터링)
    @Transactional(readOnly = true)
    public Page<LiquorSummaryDto> getLiquorsByFilters(User user, // 조회하는 사람, null 허용.
                                                      Pageable pageable,
                                                      LiquorCategory category, // 주류 대분류
                                                      LiquorSubcategory subcategory) { // 주류 소분류

        if (category == null && subcategory == null) { // 전체 주류 조회
            return liquorRepository.findAllByIsHiddenFalse(pageable)
                    .map(liquor -> LiquorMapper.toSummaryDto(liquor, user));
        }
        else if (category != null && subcategory == null) {
            return liquorRepository.findByCategoryAndIsHiddenFalse(pageable, category)
                    .map(liquor -> LiquorMapper.toSummaryDto(liquor, user)); // 대분류로 필터링
        }
        else /* (subcategory != null) */ {
            return liquorRepository.findBySubcategoryAndIsHiddenFalse(pageable, subcategory)
                    .map(liquor -> LiquorMapper.toSummaryDto(liquor, user)); // 소분류로 필터링
        }
    }

    // 2. 주류 목록 검색 (이름으로)
    @Transactional(readOnly = true)
    public Page<LiquorSummaryDto> searchLiquorsByName(User user, Pageable pageable, String keyword) {
        return liquorRepository.findByNameContainingAndIsHiddenFalse(pageable, keyword)
                .map(liquor -> LiquorMapper.toSummaryDto(liquor, user));
    }

    // 3. 주류 상세 페이지
    @Transactional(readOnly = true)
    public LiquorResponseDto getLiquorDetail(Long liquorId, User user) {

        // 주류 정보 조회 (삭제되지 않은 것만)
        Liquor liquor = liquorRepository.findByIdAndIsHiddenFalse(liquorId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 주류입니다."));

        return LiquorMapper.toDto(liquor, user);
    }

    /**
     * 이하는 관리자용 메서드들입니다.
     */

    // 주류 등록
    public LiquorResponseDto create(LiquorRequestDto dto) {
        Liquor liquor = Liquor.builder()
                .name(dto.getName())
                .category(dto.getCategory())
                .subcategory(dto.getSubcategory())
                .country(dto.getCountry())
                .manufacturer(dto.getManufacturer())
                .abv(dto.getAbv())
                .imageUrl(dto.getImageUrl())
                .build();
        return LiquorMapper.toDto(liquorRepository.save(liquor), null);
    }

    // 주류 수정
    public LiquorResponseDto update(Long id, LiquorRequestDto dto) {
        Liquor liquor = liquorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 주류입니다."));
        liquor.updateFromDto(dto);
        return LiquorMapper.toDto(liquorRepository.save(liquor), null);
    }

    // 주류 숨기기
    public void toggleHidden(Long id) {
        Liquor liquor = liquorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 주류입니다."));
        liquor.setHidden(!liquor.isHidden());
    }

    // 주류 삭제
    public void delete(Long id) {
        Liquor liquor = liquorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 주류입니다."));
        liquorRepository.delete(liquor);
    }

}
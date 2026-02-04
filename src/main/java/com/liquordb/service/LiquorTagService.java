package com.liquordb.service;

import com.liquordb.dto.liquor.LiquorResponseDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.dto.tag.LiquorTagRequestDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.*;
import com.liquordb.exception.liquor.LiquorNotFoundException;
import com.liquordb.exception.tag.TagNotFoundException;
import com.liquordb.exception.user.UserNotFoundException;
import com.liquordb.mapper.LiquorMapper;
import com.liquordb.mapper.LiquorTagMapper;
import com.liquordb.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LiquorTagService {

    private final LiquorRepository liquorRepository;
    private final TagRepository tagRepository;
    private final LiquorTagRepository liquorTagRepository;
    private final LiquorLikeRepository liquorLikeRepository;

    // 태그 이름으로 주류 검색
    @Transactional(readOnly = true)
    public List<LiquorSummaryDto> getLiquorsByTagName(String tagName, UUID userId) {
        List<Liquor> liquors = liquorTagRepository.findLiquorsByTagName(tagName);
        List<Long> liquorIds = liquors.stream().map(Liquor::getId).toList();

        Set<Long> likedLiquorIds = (userId != null)
                ? liquorLikeRepository.findLikedLiquorIdsByUserIdAndLiquorIds(userId, liquorIds)
                : Collections.emptySet();

        List<LiquorSummaryDto> response = liquors.stream().map(liquor -> {
            boolean isLiked = likedLiquorIds.contains(liquor.getId());
            return LiquorMapper.toSummaryDto(liquor, isLiked, liquor.getReviewCount(), liquor.getLikeCount());
        }).toList();

        return response;
    }

    // 특정 주류에 연결된 태그 이름 목록 반환
    @Transactional(readOnly = true)
    public List<TagResponseDto> getTagsByLiquorId(Long liquorId) {
        return liquorTagRepository.findTagsByLiquorId(liquorId).stream()
                .map(LiquorTagMapper::toTagDto)
                .toList();
    }

    /**
     * 관리자용
     */
    // 주류에 태그 추가
    public TagResponseDto add(LiquorTagRequestDto request) {

        Liquor liquor = liquorRepository.findById(request.liquorId())
                .orElseThrow(() -> new LiquorNotFoundException(request.liquorId()));

        Tag tag = tagRepository.findById(request.tagId())
                .orElseThrow(() -> new TagNotFoundException(request.tagId()));

        LiquorTag liquorTag = LiquorTagMapper.toEntity(liquor, tag);
        liquorTagRepository.save(liquorTag);

        return LiquorTagMapper.toTagDto(liquorTag);
    }
}
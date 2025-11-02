package com.liquordb.service;

import com.liquordb.dto.liquor.LiquorResponseDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.dto.liquor.LiquorTagRequestDto;
import com.liquordb.dto.liquor.LiquorTagResponseDto;
import com.liquordb.entity.*;
import com.liquordb.enums.UserStatus;
import com.liquordb.exception.LiquorNotFoundException;
import com.liquordb.exception.TagNotFoundException;
import com.liquordb.exception.UserNotFoundException;
import com.liquordb.mapper.LiquorMapper;
import com.liquordb.mapper.LiquorTagMapper;
import com.liquordb.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LiquorTagService {

    private final UserRepository userRepository;
    private final UserTagRepository userTagRepository;
    private final LiquorRepository liquorRepository;
    private final TagRepository tagRepository;
    private final LiquorTagRepository liquorTagRepository;

    // 태그 이름으로 주류 검색
    @Transactional(readOnly = true)
    public List<LiquorResponseDto> getLiquorsByTagName(String tagName, User user) {
        return liquorTagRepository.findLiquorsByTagName(tagName).stream()
                .map(liquor -> LiquorMapper.toDto(liquor, user))
                .toList();
    }

    // 태그 ID로 주류 검색
    @Transactional(readOnly = true)
    public List<LiquorResponseDto> getLiquorsByTagId(Long tagId, User user) {
        return liquorTagRepository.findLiquorByTagId(tagId).stream()
                .map(liquorTag -> LiquorMapper.toDto(liquorTag.getLiquor(), user))
                .toList();
    }

    // 특정 주류에 연결된 태그 이름 목록 반환
    @Transactional(readOnly = true)
    public List<LiquorTagResponseDto> getTagsByLiquorId(Long liquorId) {
        return liquorTagRepository.findTagsByLiquorId(liquorId).stream()
                .map(LiquorTagMapper::toDto)
                .toList();
    }

    /**
     * 관리자용
     */
    // 주류에 태그 추가
    public LiquorTagResponseDto addLiquorTag(LiquorTagRequestDto request) {

        Liquor liquor = liquorRepository.findById(request.getLiquorId())
                .orElseThrow(() -> new LiquorNotFoundException(request.getLiquorId()));

        Tag tag = tagRepository.findById(request.getTagId())
                .orElseThrow(() -> new TagNotFoundException(request.getTagId()));

        LiquorTag liquorTag = LiquorTagMapper.toEntity(liquor, tag);
        liquorTagRepository.save(liquorTag);

        return LiquorTagMapper.toDto(liquorTag);
    }
}
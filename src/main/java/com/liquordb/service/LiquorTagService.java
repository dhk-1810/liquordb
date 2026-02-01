package com.liquordb.service;

import com.liquordb.dto.liquor.LiquorResponseDto;
import com.liquordb.dto.tag.LiquorTagRequestDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.*;
import com.liquordb.exception.liquor.LiquorNotFoundException;
import com.liquordb.exception.tag.TagNotFoundException;
import com.liquordb.mapper.LiquorMapper;
import com.liquordb.mapper.LiquorTagMapper;
import com.liquordb.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LiquorTagService {

    private final LiquorRepository liquorRepository;
    private final TagRepository tagRepository;
    private final LiquorTagRepository liquorTagRepository;
    private final LiquorMapper liquorMapper;

    // 태그 이름으로 주류 검색
    @Transactional(readOnly = true)
    public List<LiquorResponseDto> getLiquorsByTagName(String tagName, User user) {
        return liquorTagRepository.findLiquorsByTagName(tagName).stream()
                .map(liquor -> liquorMapper.toDto(liquor, user))
                .toList();
    }

    // 태그 ID로 주류 검색
    @Transactional(readOnly = true)
    public List<LiquorResponseDto> getLiquorsByTagId(Long tagId, User user) {
        return liquorTagRepository.findLiquorByTagId(tagId).stream()
                .map(liquorTag -> liquorMapper.toDto(liquorTag.getLiquor(), user))
                .toList();
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
    public TagResponseDto addLiquorTag(LiquorTagRequestDto request) {

        Liquor liquor = liquorRepository.findById(request.liquorId())
                .orElseThrow(() -> new LiquorNotFoundException(request.liquorId()));

        Tag tag = tagRepository.findById(request.tagId())
                .orElseThrow(() -> new TagNotFoundException(request.tagId()));

        LiquorTag liquorTag = LiquorTagMapper.toEntity(liquor, tag);
        liquorTagRepository.save(liquorTag);

        return LiquorTagMapper.toTagDto(liquorTag);
    }
}
package com.liquordb.service;

import com.liquordb.dto.tag.LiquorTagRequest;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.*;
import com.liquordb.exception.liquor.LiquorNotFoundException;
import com.liquordb.exception.tag.TagNotFoundException;
import com.liquordb.mapper.LiquorTagMapper;
import com.liquordb.mapper.TagMapper;
import com.liquordb.repository.*;
import com.liquordb.repository.liquor.LiquorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LiquorTagService {

    private final LiquorRepository liquorRepository;
    private final TagRepository tagRepository;
    private final LiquorTagRepository liquorTagRepository;

    // 특정 주류에 연결된 태그 이름 목록 반환
    // TODO 주류 단건조회로 이동, FETCH JOIN?
    @Transactional(readOnly = true)
    public List<TagResponseDto> getTagsByLiquorId(Long liquorId) {
        return liquorTagRepository.findTagsByLiquorId(liquorId).stream()
                .map(TagMapper::toDto)
                .toList();
    }

    /**
     * 관리자용
     */
    // 주류에 태그 추가
    public TagResponseDto add(LiquorTagRequest request) {

        Liquor liquor = liquorRepository.findById(request.liquorId())
                .orElseThrow(() -> new LiquorNotFoundException(request.liquorId()));

        Tag tag = tagRepository.findById(request.tagId())
                .orElseThrow(() -> new TagNotFoundException(request.tagId()));

        LiquorTag liquorTag = LiquorTagMapper.toEntity(liquor, tag);
        liquorTagRepository.save(liquorTag);

        return TagMapper.toDto(liquorTag);
    }
}
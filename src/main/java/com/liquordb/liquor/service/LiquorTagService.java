package com.liquordb.liquor.service;

import com.liquordb.liquor.dto.LiquorTagRequestDto;
import com.liquordb.liquor.dto.LiquorTagResponseDto;
import com.liquordb.liquor.entity.Liquor;
import com.liquordb.liquor.entity.LiquorTag;
import com.liquordb.liquor.mapper.LiquorTagMapper;
import com.liquordb.liquor.repository.LiquorRepository;
import com.liquordb.liquor.repository.LiquorTagRepository;
import com.liquordb.tag.entity.Tag;
import com.liquordb.tag.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
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

    // 태그 이름으로 주류 검색
    @Transactional(readOnly = true)
    public List<Liquor> getLiquorsByTagName(String tagName) {
        return liquorTagRepository.findLiquorsByTagName(tagName);
    }

    // 태그 ID로 주류 검색
    @Transactional(readOnly = true)
    public List<Liquor> getLiquorsByTagId(Long tagId) {
        return liquorTagRepository.findLiquorByTagId(tagId)
                .stream()
                .map(LiquorTag::getLiquor)
                .toList();
    }

    // 주류에 태그 추가
    public LiquorTagResponseDto addLiquorTag(LiquorTagRequestDto requestDto) {
        Liquor liquor = liquorRepository.findById(requestDto.getLiquorId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 주류입니다."));
        Tag tag = tagRepository.findById(requestDto.getTagId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 태그입니다."));

        LiquorTag liquorTag = LiquorTagMapper.toEntity(liquor, tag);
        liquorTagRepository.save(liquorTag);

        return LiquorTagMapper.toDto(liquorTag);
    }
}
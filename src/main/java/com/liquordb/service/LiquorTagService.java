package com.liquordb.service;

import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.dto.liquor.LiquorTagRequestDto;
import com.liquordb.dto.liquor.LiquorTagResponseDto;
import com.liquordb.entity.*;
import com.liquordb.enums.UserStatus;
import com.liquordb.exception.NotFoundException;
import com.liquordb.mapper.LiquorMapper;
import com.liquordb.mapper.LiquorTagMapper;
import com.liquordb.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public List<Liquor> getLiquorsByTagName(String tagName) {
        return liquorTagRepository.findLiquorsByTagName(tagName);
    }

    // 태그 ID로 주류 검색
    @Transactional(readOnly = true)
    public List<Liquor> getLiquorsByTagId(Long tagId) {
        return liquorTagRepository.findLiquorByTagId(tagId).stream()
                .map(LiquorTag::getLiquor)
                .toList();
    }

    // 특정 주류에 연결된 태그 이름 목록 반환
    @Transactional(readOnly = true)
    public Set<LiquorTagResponseDto> getTagResponseDtos(Long liquorId) {
        return liquorTagRepository.findTagsByLiquorId(liquorId).stream()
                .map(LiquorTagMapper::toDto)
                .collect(Collectors.toSet());
    }

    // 유저가 선호하는 태그로 주류 목록 조회
    @Transactional(readOnly = true)
    public List<LiquorSummaryDto> getLiquorsByUserTags(UUID userId) {

        User user = userRepository.findByIdAndStatusNot(userId, UserStatus.WITHDRAWN)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));

        List<UserTag> userTags = userTagRepository.findByUserId(userId);
        List<Liquor> liquors = userTags.stream()
                .flatMap(ut -> ut.getTag().getLiquorTags().stream()
                        .map(LiquorTag::getLiquor))
                .distinct()
                .toList();

        return liquors.stream()
                .map(liquor -> LiquorMapper.toSummaryDto(liquor, user))
                .distinct()
                .toList();
    }

    /**
     * 관리자용
     */
    // 주류에 태그 추가
    public LiquorTagResponseDto addLiquorTag(LiquorTagRequestDto requestDto) {

        Liquor liquor = liquorRepository.findById(requestDto.getLiquorId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 주류입니다."));

        Tag tag = tagRepository.findById(requestDto.getTagId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 태그입니다."));

        LiquorTag liquorTag = LiquorTagMapper.toEntity(liquor, tag);
        liquorTagRepository.save(liquorTag);

        return LiquorTagMapper.toDto(liquorTag);
    }
}
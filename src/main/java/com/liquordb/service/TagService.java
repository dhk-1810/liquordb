package com.liquordb.service;

import com.liquordb.dto.tag.TagRequestDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.Tag;
import com.liquordb.exception.tag.TagNotFoundException;
import com.liquordb.mapper.TagMapper;
import com.liquordb.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    /**
     * 이하는 관리자용 메서드임.
     */
    // 태그 등록
    @Transactional
    public TagResponseDto create(TagRequestDto request) {
        Tag tag = TagMapper.toEntity(request);
        tagRepository.save(tag);
        return TagMapper.toDto(tag);
    }

    // 태그 이름 변경
    @Transactional
    public TagResponseDto rename(Long id, TagRequestDto request) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));
        tag.update(request);
        tagRepository.save(tag);
        return TagMapper.toDto(tag);
    }

    // 태그 전체 목록 조회
    // TODO 페이지네이션
    @Transactional(readOnly = true)
    public List<TagResponseDto> findAll() {
        List<Tag> tags = tagRepository.findAllByIsDeletedFalse();
        return tags.stream().map(TagMapper::toDto).toList();

    }

    // 태그 삭제
    @Transactional
    public void delete(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));
        tag.softDelete();
        tagRepository.save(tag);
    }
}

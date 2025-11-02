package com.liquordb.service;

import com.liquordb.dto.tag.TagRequestDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.Tag;
import com.liquordb.exception.TagNotFoundException;
import com.liquordb.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    /**
     * 이하는 관리자용 메서드입니다.
     */
    // 태그 등록
    @Transactional
    public TagResponseDto create(TagRequestDto requestDto) {
        Tag tag = Tag.builder()
                .name(requestDto.getName())
                .build();
        tagRepository.save(tag);
        return TagResponseDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }

    // 태그 이름 변경
    @Transactional
    public TagResponseDto rename(Long id, TagRequestDto dto){
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));
        tag.setName(dto.getName()); // 이름 변경
        tagRepository.save(tag);

        return TagResponseDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }

    // 태그 삭제
    @Transactional
    public void delete(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));
        tag.setDeleted(true);
        tag.setDeletedAt(LocalDateTime.now());
        tagRepository.save(tag);
    }
}

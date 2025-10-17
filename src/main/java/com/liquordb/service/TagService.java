package com.liquordb.service;

import com.liquordb.dto.tag.TagRequestDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.Tag;
import com.liquordb.exception.NotFoundException;
import com.liquordb.mapper.TagMapper;
import com.liquordb.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new NotFoundException("존재하지 않는 태그입니다. ID=" + id));
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
        if (!tagRepository.existsById(id)) {
            throw new NotFoundException("해당 ID의 태그가 존재하지 않습니다. ID=" + id);
        }
        tagRepository.deleteById(id);
    }
}

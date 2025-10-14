package com.liquordb.service;

import com.liquordb.dto.tag.TagRequestDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.Tag;
import com.liquordb.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    // 특정 주류에 연결된 태그 이름 목록 반환
    @Transactional(readOnly = true)
    public List<String> getTagsForLiquor(Long liquorId) {
        return tagRepository.findTagsByLiquorId(liquorId).stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }

    // 특정 유저의 선호 태그 목록 반환 (10개 제한 여부 선택 가능)
    @Transactional(readOnly = true)
    public List<String> getPreferredTagsForUser(UUID userId, boolean showAll) {
        List<String> allTags = tagRepository.findTagsByUserId(userId).stream()
                .map(Tag::getName)
                .collect(Collectors.toList());

        if (showAll || allTags.size() <= 10) {
            return allTags;
        }
        return allTags.subList(0, 10);
    }

    /**
     * 이하는 관리자용 메서드입니다.
     */
    // 태그 등록
    @Transactional
    public TagResponseDto createTag(TagRequestDto requestDto) {
        Tag tag = Tag.builder()
                .name(requestDto.getName())
                .build();
        Tag savedTag = tagRepository.save(tag);
        return new TagResponseDto(savedTag.getId(), savedTag.getName());
    }

    // 태그 이름 변경
    @Transactional
    public TagResponseDto renameTag(Long id, TagRequestDto requestDto){
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 태그가 존재하지 않습니다. ID=" + id));

        tag.setName(requestDto.getName()); // 이름 변경
        Tag updatedTag = tagRepository.save(tag);

        return new TagResponseDto(updatedTag.getId(), updatedTag.getName());
    }

    // 태그 삭제
    @Transactional
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 ID의 태그가 존재하지 않습니다. ID=" + id);
        }
        tagRepository.deleteById(id);
    }
}

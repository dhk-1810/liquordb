package com.liquordb.service;

import com.liquordb.dto.PageResponse;
import com.liquordb.dto.tag.TagListGetRequest;
import com.liquordb.dto.tag.TagRequest;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.entity.Tag;
import com.liquordb.enums.SortDirection;
import com.liquordb.exception.tag.TagNotFoundException;
import com.liquordb.mapper.TagMapper;
import com.liquordb.repository.tag.TagRepository;
import com.liquordb.repository.tag.TagSearchCondition;
import com.liquordb.repository.user.UserSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    /**
     * 이하는 관리자용 메서드
     */
    // 태그 등록
    @Transactional
    public TagResponseDto create(TagRequest request) {
        Tag tag = TagMapper.toEntity(request);
        tagRepository.save(tag);
        return TagMapper.toDto(tag);
    }

    // 태그 이름 변경
    @Transactional
    public TagResponseDto rename(Long id, TagRequest request) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));
        tag.update(request);
        tagRepository.save(tag);
        return TagMapper.toDto(tag);
    }

    // 태그 전체 목록 조회
    @Transactional(readOnly = true)
    public PageResponse<TagResponseDto> getAll(TagListGetRequest request) {

        int page = request.page() == null
                ? 0
                : request.page();
        int limit = request.limit() == null
                ? 50
                : request.limit();
        boolean descending = request.sortDirection() == SortDirection.DESC;
        TagSearchCondition condition = new TagSearchCondition(request.keyword(), page, limit, descending);

        Page<TagResponseDto> tags = tagRepository.findAll(condition)
                .map(TagMapper::toDto);

        return PageResponse.from(tags);
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

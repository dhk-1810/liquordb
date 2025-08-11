package com.liquordb.tag.service;

import com.liquordb.tag.entity.Tag;
import com.liquordb.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public List<String> getPreferredTagsForUser(Long userId, boolean showAll) {
        List<String> allTags = tagRepository.findTagsByUserId(userId).stream()
                .map(Tag::getName)
                .collect(Collectors.toList());

        if (showAll || allTags.size() <= 10) {
            return allTags;
        }
        return allTags.subList(0, 10);
    }
}

package com.liquordb.service;

import com.liquordb.dto.tag.UserTagRequestDto;
import com.liquordb.entity.*;
import com.liquordb.enums.UserStatus;
import com.liquordb.exception.TagNotFoundException;
import com.liquordb.exception.UserNotFoundException;
import com.liquordb.repository.TagRepository;
import com.liquordb.repository.UserRepository;
import com.liquordb.repository.UserTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserTagService {

    private final UserRepository userRepository;
    private final UserTagRepository userTagRepository;
    private final TagRepository tagRepository;

    // 선호하는 태그로 추가
    @Transactional
    public UserTag create(UserTagRequestDto dto) {
        User user = userRepository.findByIdAndStatusNot(dto.getUserId(), UserStatus.WITHDRAWN)
                .orElseThrow(() -> new UserNotFoundException(dto.getUserId()));
        Tag tag = tagRepository.findById(dto.getTagId())
                .orElseThrow(() -> new TagNotFoundException(dto.getTagId()));
        UserTagId id = new UserTagId(user.getId(), tag.getId());
        UserTag userTag = UserTag.builder()
                .id(id)
                .user(user)
                .tag(tag)
                .build();
        return userTagRepository.save(userTag);
    }

    // 선호 태그 목록 반환
    // TODO (10개 제한 여부 선택 가능)
    @Transactional(readOnly = true)
    public List<String> findTagNamesByUserId(UUID userId) {
        return userTagRepository.findTagsByUserId(userId).stream()
                .map(Tag::getName)
                .toList();
    }

    // 선호하는 태그에서 삭제
    @Transactional
    public void deleteByUserIdAndTagId(UserTagRequestDto dto) {
        User user = userRepository.findByIdAndStatusNot(dto.getUserId(), UserStatus.WITHDRAWN)
                .orElseThrow(() -> new UserNotFoundException(dto.getUserId()));
        Tag tag = tagRepository.findById(dto.getTagId())
                .orElseThrow(() -> new TagNotFoundException(dto.getTagId()));
        userTagRepository.deleteByUserIdAndTagId(user.getId(), tag.getId());
    }
}

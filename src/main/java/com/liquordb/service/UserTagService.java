package com.liquordb.service;

import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.dto.tag.UserTagRequestDto;
import com.liquordb.entity.*;
import com.liquordb.exception.tag.TagNotFoundException;
import com.liquordb.exception.tag.UserTagAlreadyExistsException;
import com.liquordb.exception.tag.UserTagNotFoundException;
import com.liquordb.exception.user.UserNotFoundException;
import com.liquordb.mapper.LiquorMapper;
import com.liquordb.mapper.TagMapper;
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

    // 선호하는 태그 목록에 추가
    @Transactional
    public UserTag add(UserTagRequestDto request, UUID userId) {

        Long tagId = request.tagId();
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException(request.tagId()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (userTagRepository.existsByUserAndTag_Id(user, tagId)) {
            throw new UserTagAlreadyExistsException(userId, tagId);
        }

        UserTag userTag = UserTag.create(user, tag);
        return userTagRepository.save(userTag);
    }

    // 선호 태그 목록 반환
    @Transactional(readOnly = true)
    public List<TagResponseDto> getByUserId(UUID userId , boolean showAll) { // TODO showAll 처리

        // TODO (10개 제한 여부 선택 가능)
        return userTagRepository.findTagsByUserId(userId).stream()
                .map(TagMapper::toDto)
                .toList();

    }

    // 유저가 선호하는 태그로 주류 목록 조회
    // TODO LiquorTagService랑 중복되지 않나
    @Transactional(readOnly = true)
    public List<LiquorSummaryDto> getRecommendedLiquors(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Liquor> liquors = userTagRepository.findLiquorsByUser_Id(userId);

        return liquors.stream()
                .map(liquor -> LiquorMapper.toSummaryDto(liquor, user))
                .toList();
    }

    // 선호하는 태그에서 삭제
    @Transactional
    public void delete(Long tagId, UUID userId) {
        UserTag userTag = userTagRepository.findById(tagId)
                .orElseThrow(() -> new UserTagNotFoundException(userId, tagId));
        userTagRepository.deleteByUser_IdAndTag_Id(userId, userTag.getTag().getId());
    }
}

package com.liquordb.service;

import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.dto.tag.TagResponseDto;
import com.liquordb.dto.tag.UserTagRequestDto;
import com.liquordb.entity.*;
import com.liquordb.enums.UserStatus;
import com.liquordb.exception.tag.TagNotFoundException;
import com.liquordb.exception.tag.UserTagAlreadyExistsException;
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

    // 선호하는 태그로 등록
    @Transactional
    public UserTag add(UserTagRequestDto request, User requestUser) {

        Tag tag = tagRepository.findById(request.tagId())
                .orElseThrow(() -> new TagNotFoundException(request.tagId()));

        if (userTagRepository.existsUserTagByUserAndTag_Id(requestUser, request.tagId())) {
            throw new UserTagAlreadyExistsException(request.tagId());
        }

        UserTag userTag = UserTag.create(requestUser, tag);
        return userTagRepository.save(userTag);
    }

    // 선호 태그 목록 반환
    @Transactional(readOnly = true)
    public List<TagResponseDto> findByUserId(UUID userId , boolean showAll) {

        // TODO (10개 제한 여부 선택 가능)
        return userTagRepository.findTagsByUserId(userId).stream()
                .map(TagMapper::toDto)
                .toList();

    }

    // 유저가 선호하는 태그로 주류 목록 조회
    @Transactional(readOnly = true)
    public List<LiquorSummaryDto> getLiquorsByUserTags(UUID userId) {

        User user = userRepository.findByIdAndStatusNot(userId, UserStatus.WITHDRAWN)
                .orElseThrow(() -> new UserNotFoundException(userId));

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

    // 선호하는 태그에서 삭제
    @Transactional
    public void deleteByUserIdAndTagId(User requestUser, UserTagRequestDto request) {
        Tag tag = tagRepository.findById(request.tagId())
                .orElseThrow(() -> new TagNotFoundException(request.tagId()));
        userTagRepository.deleteByUserIdAndTagId(requestUser.getId(), tag.getId());
    }
}

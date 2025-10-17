package com.liquordb.service;

import com.liquordb.dto.liquor.LiquorLikeResponseDto;
import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.entity.LiquorLike;
import com.liquordb.exception.NotFoundException;
import com.liquordb.mapper.LiquorMapper;
import com.liquordb.repository.LiquorLikeRepository;
import com.liquordb.entity.Liquor;
import com.liquordb.repository.LiquorRepository;
import com.liquordb.entity.User;
import com.liquordb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LiquorLikeService {

    private final LiquorRepository liquorRepository;
    private final LiquorLikeRepository liquorLikeRepository;
    private final UserRepository userRepository;

    // 좋아요 토글 (누르기/취소)
    @Transactional
    public LiquorLikeResponseDto toggleLike(UUID userId, Long liquorId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));

        Liquor liquor = liquorRepository.findById(liquorId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 주류입니다."));

        Optional<LiquorLike> optionalLiquorLike = liquorLikeRepository
                .findByUserIdAndLiquorId(user.getId(), liquor.getId());

        if (optionalLiquorLike.isPresent()) { // 이미 좋아요 눌려있으면 취소
            liquorLikeRepository.delete(optionalLiquorLike.get());
            return buildResponse(userId, liquorId, false, null);
        } else {
            LiquorLike newLike = LiquorLike.builder()
                            .user(user)
                            .liquor(liquor)
                            .likedAt(LocalDateTime.now())
                            .build();

            LiquorLike saved = liquorLikeRepository.save(newLike);

            return buildResponse(userId, liquorId, true, saved.getLikedAt());
        }
    }

    // 좋아요 카운트
    @Transactional(readOnly = true)
    public long countByLiquorIdAndLikedTrue(Long liquorId) {
        return liquorLikeRepository.countByLiquorIdAndLikedTrue(liquorId);
    }

    // 유저가 좋아요 누른 주류 목록
    @Transactional(readOnly = true)
    public List<LiquorSummaryDto> getLiquorSummaryDtosByUserId(UUID userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 유저입니다."));
        return liquorLikeRepository.findByUserIdAndLiquorIsHiddenFalse(userId)
                .stream()
                .map(liquorLike -> LiquorMapper.toSummaryDto(liquorLike.getLiquor(), user))
                .toList();
    }

    // DTO build
    private LiquorLikeResponseDto buildResponse(UUID userId, Long liquorId, boolean liked, LocalDateTime likedAt) {
        return LiquorLikeResponseDto.builder()
                // .id(id)
                .userId(userId)
                .liquorId(liquorId)
                .liked(liked)
                .likedAt(likedAt)
                .build();
    }
}

package com.liquordb.service;

import com.liquordb.dto.LikeResponseDto;
import com.liquordb.entity.LiquorLike;
import com.liquordb.event.LiquorLikeEvent;
import com.liquordb.exception.liquor.LiquorNotFoundException;
import com.liquordb.repository.LiquorLikeRepository;
import com.liquordb.entity.Liquor;
import com.liquordb.repository.LiquorRepository;
import com.liquordb.entity.User;
import com.liquordb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LiquorLikeService {

    private final LiquorRepository liquorRepository;
    private final LiquorLikeRepository liquorLikeRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public LikeResponseDto like(Long liquorId, UUID userId) {

        if (liquorLikeRepository.existsByLiquor_IdAndUser_Id(liquorId, userId)) {
            return new LikeResponseDto(true, liquorLikeRepository.countByLiquor_Id(liquorId));
        }

        User user = userRepository.getReferenceById(userId);
        Liquor liquor = liquorRepository.getReferenceById(liquorId);

        LiquorLike liquorLike = LiquorLike.create(user, liquor);

        // 주류 페이지 접근 이후, 좋아요 누르기 이전 시점에 주류가 삭제된 경우의 예외 처리
        try {
            liquorLikeRepository.save(liquorLike);
        } catch (DataIntegrityViolationException e) {
            throw new LiquorNotFoundException(liquorId);
        }

        // 비동기로 엔터티의 likeCount 1 증가
        eventPublisher.publishEvent(new LiquorLikeEvent(liquorId, true));

        long likeCount = liquorLikeRepository.countByLiquor_Id(liquorId);
        return new LikeResponseDto(true, likeCount);
    }

    @Transactional
    public LikeResponseDto cancelLike(Long liquorId, UUID userId) {
        liquorLikeRepository.findByLiquor_IdAndUser_Id(liquorId, userId)
                .ifPresent(liquorLikeRepository::delete);

        eventPublisher.publishEvent(new LiquorLikeEvent(liquorId, false)); // 엔터티의 likeCount 1 감소
        long likeCount = liquorLikeRepository.countByLiquor_Id(liquorId);
        return new LikeResponseDto(false, likeCount);
    }

}

package com.liquordb.service;

import com.liquordb.entity.LiquorLike;
import com.liquordb.event.LiquorLikeEvent;
import com.liquordb.exception.liquor.LiquorLikeAlreadyExistsException;
import com.liquordb.exception.liquor.LiquorLikeNotFoundException;
import com.liquordb.exception.liquor.LiquorNotFoundException;
import com.liquordb.repository.LiquorLikeRepository;
import com.liquordb.entity.Liquor;
import com.liquordb.repository.liquor.LiquorRepository;
import com.liquordb.entity.User;
import com.liquordb.repository.user.UserRepository;
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
    public void like(Long liquorId, UUID userId) {

        if (liquorLikeRepository.existsByLiquor_IdAndUser_Id(liquorId, userId)) {
            throw new LiquorLikeAlreadyExistsException(liquorId, userId);
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
    }

    @Transactional
    public void cancelLike(Long liquorId, UUID userId) {

        LiquorLike liquorlike = liquorLikeRepository.findByLiquor_IdAndUser_Id(liquorId, userId)
                .orElseThrow(() -> new LiquorLikeNotFoundException(liquorId, userId));

        liquorLikeRepository.delete(liquorlike);
        eventPublisher.publishEvent(new LiquorLikeEvent(liquorId, false)); // 엔터티의 likeCount 1 감소
    }

}

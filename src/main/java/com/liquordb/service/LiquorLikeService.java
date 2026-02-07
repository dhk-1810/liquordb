package com.liquordb.service;

import com.liquordb.dto.LikeResponseDto;
import com.liquordb.entity.LiquorLike;
import com.liquordb.exception.liquor.LiquorNotFoundException;
import com.liquordb.exception.user.UserNotFoundException;
import com.liquordb.repository.LiquorLikeRepository;
import com.liquordb.entity.Liquor;
import com.liquordb.repository.LiquorRepository;
import com.liquordb.entity.User;
import com.liquordb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LiquorLikeService {

    private final LiquorRepository liquorRepository;
    private final LiquorLikeRepository liquorLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public LikeResponseDto like(Long liquorId, UUID userId) {

        if (liquorLikeRepository.existsByLiquor_IdAndUser_Id(liquorId, userId)) {
            return new LikeResponseDto(true, liquorLikeRepository.countByLiquor_Id(liquorId));
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Liquor liquor = liquorRepository.findById(liquorId)
                .orElseThrow(() -> new LiquorNotFoundException(liquorId));

        LiquorLike liquorLike = LiquorLike.create(user, liquor);
        liquorLikeRepository.save(liquorLike);
        long likeCount = liquorLikeRepository.countByLiquor_Id(liquorId);
        return new LikeResponseDto(true, likeCount);
    }

    @Transactional
    public LikeResponseDto cancelLike(Long liquorId, UUID userId) {
        liquorLikeRepository.findByLiquor_IdAndUser_Id(liquorId, userId)
                .ifPresent(liquorLikeRepository::delete);

        long likeCount = liquorLikeRepository.countByLiquor_Id(liquorId);
        return new LikeResponseDto(false, likeCount);
    }

}

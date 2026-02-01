package com.liquordb.service;

import com.liquordb.dto.liquor.LiquorLikeResponseDto;
import com.liquordb.entity.LiquorLike;
import com.liquordb.enums.UserStatus;
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

@Service
@RequiredArgsConstructor
public class LiquorLikeService {

    private final LiquorRepository liquorRepository;
    private final LiquorLikeRepository liquorLikeRepository;
    private final UserRepository userRepository;

    // 좋아요 토글 (누르기/취소)
    @Transactional
    public LiquorLikeResponseDto toggleLike(UUID userId, Long liquorId) {
        User user = userRepository.findByIdAndStatusNot(userId, UserStatus.WITHDRAWN)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Liquor liquor = liquorRepository.findById(liquorId)
                .orElseThrow(() -> new LiquorNotFoundException(liquorId));

        LiquorLike existingLiquorLike = liquorLikeRepository
                .findByUserIdAndLiquorId(user.getId(), liquor.getId()).orElse(null);

        if (existingLiquorLike != null) {
            liquorLikeRepository.delete(existingLiquorLike);
            return null;
        } else {
            LiquorLike liquorLike = LiquorLike.create(user, liquor);
            liquorLikeRepository.save(liquorLike);
            return LiquorLikeResponseDto.toDto(liquorLike);
        }
    }

}

package com.liquordb.dto.liquor;

import com.liquordb.entity.LiquorLike;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record LiquorLikeResponseDto (
    Long id,                    // TODO 좋아요 ID - 딱히 쓸모가 없음. 삭제?
    boolean liked,
    UUID userId,                // 좋아요 누른 유저 ID
    Long liquorId,              // 대상 주류 ID
    LocalDateTime likedAt      // 좋아요 누른 시각
) {
    public static LiquorLikeResponseDto toDto(LiquorLike liquorLike){
        return LiquorLikeResponseDto.builder()
                .id(liquorLike.getId())
                .userId(liquorLike.getUser().getId())
                .liquorId(liquorLike.getLiquor().getId())
                .liked(liquorLike.isLiked())
                .likedAt(liquorLike.getLikedAt())
                .build();
    }
}

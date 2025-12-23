package com.liquordb.dto.liquor;

import com.liquordb.entity.LiquorLike;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record LiquorLikeResponseDto (
    Long id,                    // TODO 딱히 쓸모가 없음. 삭제?
    boolean liked,
    UUID userId,
    Long liquorId,
    LocalDateTime likedAt
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

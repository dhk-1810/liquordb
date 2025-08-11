package com.liquordb.like.dto;

import com.liquordb.like.entity.LikeTargetType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeRequestDto {
    private Long targetId;
    private LikeTargetType targetType;
}

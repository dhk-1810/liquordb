package com.liquordb.like.dto;

import com.liquordb.like.entity.LikeTargetType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeRequestDto {
    private Long targetId;
    private LikeTargetType targetType;
}

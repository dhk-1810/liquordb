package com.liquordb.dto.tag;

import lombok.Builder;

/**
 * 유저의 선호 태그 등록
 */
@Builder
public record UserTagRequestDto (
        Long tagId
) {
}

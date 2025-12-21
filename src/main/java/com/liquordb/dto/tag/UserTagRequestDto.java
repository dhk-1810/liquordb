package com.liquordb.dto.tag;

import lombok.Builder;

@Builder
public record UserTagRequestDto (
        Long tagId
) {
}

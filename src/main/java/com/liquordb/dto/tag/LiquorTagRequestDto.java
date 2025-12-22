package com.liquordb.dto.tag;

import lombok.*;

/**
 * 주류에 태그 등록 요청
 */
@Builder
public record LiquorTagRequestDto (
        Long liquorId,
        Long tagId
){

}

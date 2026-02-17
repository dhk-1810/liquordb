package com.liquordb.dto.tag;

import lombok.*;

/**
 * 주류에 태그 등록 요청
 */
public record LiquorTagRequest(
        Long liquorId,
        Long tagId
){

}

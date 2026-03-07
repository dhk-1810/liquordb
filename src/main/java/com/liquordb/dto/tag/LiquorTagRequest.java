package com.liquordb.dto.tag;

import jakarta.validation.constraints.NotNull;

/**
 * 주류에 태그 등록 요청
 */
public record LiquorTagRequest(

        @NotNull(message = "주류 ID는 필수입니다.")
        Long liquorId,

        @NotNull(message = "태그 ID는 필수입니다.")
        Long tagId
){

}

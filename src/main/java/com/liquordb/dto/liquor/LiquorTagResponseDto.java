package com.liquordb.dto.liquor;

import com.liquordb.entity.LiquorTagId;
import lombok.*;

@Builder
public record LiquorTagResponseDto (
        LiquorTagId id,
        Long liquorId,
        Long tagId,
        String tagName
){

}

package com.liquordb.dto.liquor;

import lombok.*;

@Builder
public record LiquorTagRequestDto (
        Long liquorId,
        Long tagId
){

}

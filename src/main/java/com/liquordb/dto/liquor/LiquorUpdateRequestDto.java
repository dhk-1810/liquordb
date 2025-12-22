package com.liquordb.dto.liquor;

import lombok.Builder;

@Builder
public record LiquorUpdateRequestDto (
        Boolean isDiscontinued,
        Boolean deleteImage
){
}

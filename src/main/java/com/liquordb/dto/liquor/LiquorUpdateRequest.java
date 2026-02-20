package com.liquordb.dto.liquor;

public record LiquorUpdateRequest(
        Boolean isDiscontinued,
        Boolean deleteImage
){
}

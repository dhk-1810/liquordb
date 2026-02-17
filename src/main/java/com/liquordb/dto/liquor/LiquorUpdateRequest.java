package com.liquordb.dto.liquor;

public record LiquorUpdateRequest(
        Boolean isDiscontinued,
        Boolean deleteImage // TODO 어떤 이미지 삭제할지 정보 필요
){
}

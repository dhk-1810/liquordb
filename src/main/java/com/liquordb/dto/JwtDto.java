package com.liquordb.dto;

import com.liquordb.dto.user.UserResponseDto;
import lombok.Builder;

@Builder
public record JwtDto(
        UserResponseDto userDto,
        String accessToken
) {

}

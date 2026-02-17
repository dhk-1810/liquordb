package com.liquordb.dto;

import com.liquordb.dto.user.UserResponseDto;

public record JwtDto(
        UserResponseDto userDto,
        String accessToken
) {

}

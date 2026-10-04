package com.liquordb.dto.auth;

import com.liquordb.dto.user.UserResponseDto;

public record AuthResponseDto (
        UserResponseDto user,
        String accessToken
) {

}

package com.liquordb.security;

import com.liquordb.dto.user.UserResponseDto;

import java.util.UUID;

public record JwtInformation (
        UserResponseDto dto,
        String accessToken,
        String refreshToken
){
    public UUID getUserId() {
        return dto.id();
    }
}
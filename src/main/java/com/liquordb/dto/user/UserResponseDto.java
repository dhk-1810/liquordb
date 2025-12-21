package com.liquordb.dto.user;

import com.liquordb.enums.UserStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserResponseDto (
        UUID id,
        String email,
        String nickname,
        String profileImageUrl,
        LocalDateTime createdAt,
        UserStatus status,
        String role
){

}
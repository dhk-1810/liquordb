package com.liquordb.dto.user;

import com.liquordb.enums.Role;
import com.liquordb.enums.UserStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserResponseDto (
        UUID id,
        String email,
        String username,
        String profileImageUrl,
        LocalDateTime createdAt,
        UserStatus status,
        Role role
){

}
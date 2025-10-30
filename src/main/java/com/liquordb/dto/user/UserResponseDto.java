package com.liquordb.dto.user;

import com.liquordb.enums.UserStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class UserResponseDto {
    private UUID id;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private UserStatus status;
    private String role;

}
package com.liquordb.user.dto;

import com.liquordb.user.entity.User;
import com.liquordb.user.entity.UserStatus;
import lombok.*;

import java.time.LocalDateTime;

/**
 * AdminUserController, AdminUserService에서 사용하는 Response DTO입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class UserAdminDto {
    private Long id;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;
    private UserStatus status;

    // Entity 객체 -> DTO 객체 변환 메서드
    public static UserAdminDto from(User user) {
        return UserAdminDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .createdAt(user.getCreatedAt())
                .status(user.getStatus())
                .build();
    }
}
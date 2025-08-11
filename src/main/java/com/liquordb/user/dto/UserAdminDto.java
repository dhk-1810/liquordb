package com.liquordb.user.dto;

import com.liquordb.user.entity.User;
import com.liquordb.user.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminDto {
    private Long id;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;
    private UserStatus status;

    // Entity 객체를 DTO 객체로 변환해주는 함수
    public static UserAdminDto from(User user) {
        UserAdminDto dto = new UserAdminDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setNickname(user.getNickname());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setStatus(user.getStatus());
        return dto;
    }
}
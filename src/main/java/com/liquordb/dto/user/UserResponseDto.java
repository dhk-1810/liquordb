package com.liquordb.dto.user;

import com.liquordb.entity.User;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class UserResponseDto {
    private Long id;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private String role;

    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole().name())
                .build();
    }
}
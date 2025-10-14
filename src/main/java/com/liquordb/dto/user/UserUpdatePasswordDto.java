package com.liquordb.dto.user;

import lombok.*;

/**
 * 유저 비밀번호 변경 DTO입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class UserUpdatePasswordDto {
    private String currentPassword;
    private String newPassword;
}
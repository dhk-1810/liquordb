package com.liquordb.user.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 유저 회원정보 변경 DTO입니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequestDto {
    private String nickname;
    private MultipartFile profileImage;
}
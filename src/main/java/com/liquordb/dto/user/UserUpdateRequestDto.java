package com.liquordb.dto.user;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 유저 회원정보 변경 DTO입니다.
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDto {
    private String nickname;
    private MultipartFile profileImage;
}
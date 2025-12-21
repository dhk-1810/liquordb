package com.liquordb.dto.user;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 유저 회원정보 수정 DTO
 */
@Builder
public record UserUpdateRequestDto (
        String email,
        String nickname,
        boolean deleteProfileImage
){

}
package com.liquordb.dto.user;

import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 유저 회원정보 수정 DTO
 */
@Builder
public record UserUpdateRequestDto (

        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        String nickname,

        boolean deleteProfileImage
){

}
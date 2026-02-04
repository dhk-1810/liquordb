package com.liquordb.controller;

import com.liquordb.dto.user.*;
import com.liquordb.enums.Role;
import com.liquordb.security.CustomUserDetails;
import com.liquordb.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 회원정보 수정 (프로필사진, 닉네임)
    @PatchMapping(path = "/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> update(
            @ModelAttribute UserUpdateRequestDto dto,
            @RequestPart(required = false) MultipartFile profileImage,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        userService.update(user.getUserId(), dto, profileImage);
        return ResponseEntity.ok("회원 정보가 수정되었습니다.");
    }

    // 비밀번호 수정 (로그인 상태에서)
    @PatchMapping("/update-password")
    public ResponseEntity<Void> updatePassword(
            @RequestBody @Valid PasswordUpdateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        userService.updatePassword(request, user.getUserId());
        return ResponseEntity.noContent().build();
    }

    // 마이페이지
    @GetMapping("/my-page")
    public ResponseEntity<UserMyPageResponseDto> getMyPage(
            @RequestParam(defaultValue = "false") boolean showAllTags,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        UserMyPageResponseDto myPage = userService.getMyPageInfo(user.getUserId(), showAllTags);
        return ResponseEntity.ok(myPage);
    }

    // 회원 탈퇴
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal CustomUserDetails user) {
        userService.withdraw(user.getUserId());
        return ResponseEntity.noContent().build();
    }

}

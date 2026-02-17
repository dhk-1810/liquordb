package com.liquordb.controller;

import com.liquordb.dto.user.*;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 마이페이지
    @GetMapping("/{userId}/my-page")
    public ResponseEntity<UserMyPageDto> getMyPage(
            @PathVariable UUID userId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        UserMyPageDto myPage = userService.getMyPageInfo(userId);
        return ResponseEntity.ok(myPage);
    }

    // 회원정보 수정 (프로필사진, 닉네임)
    @PatchMapping(path = "/{userId}/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> update(
            @PathVariable UUID userId,
            @ModelAttribute UserUpdateRequest request,
            @RequestPart(required = false) MultipartFile profileImage,
            @AuthenticationPrincipal CustomUserDetails user // 서비스단에서 @PreAuthorize 사용 위해 필요
    ) {
        userService.update(userId, request, profileImage);
        return ResponseEntity.ok("회원 정보가 수정되었습니다.");
    }

    // 비밀번호 수정 (로그인 상태에서)
    @PatchMapping("/{userId}/update-password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable UUID userId,
            @RequestBody @Valid PasswordUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        userService.updatePassword(userId, request);
        return ResponseEntity.noContent().build();
    }

    // 회원 탈퇴
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID userId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        userService.withdraw(userId);
        return ResponseEntity.noContent().build();
    }

}

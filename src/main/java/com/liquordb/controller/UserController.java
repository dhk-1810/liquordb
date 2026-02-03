package com.liquordb.controller;

import com.liquordb.dto.user.*;
import com.liquordb.enums.Role;
import com.liquordb.security.CustomUserDetails;
import com.liquordb.service.UserService;
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

    // 회원가입
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UserResponseDto> register(
            @RequestBody UserRegisterRequestDto dto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        return ResponseEntity.ok(userService.signUp(dto, profileImage, Role.USER));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody UserLoginRequestDto request) {
        return ResponseEntity.ok(userService.login(request));
    }

    // 비밀번호 재설정 링크 전송
    @PostMapping("/find-password")
    public ResponseEntity<String> sendPasswordResetEmail(@RequestBody UserFindPasswordRequestDto request) {
        return ResponseEntity.ok("이메일로 재설정 링크를 전송했습니다.");
    }

    // 비밀번호 재설정
    @PatchMapping("/update-password")
    public ResponseEntity<String> updatePassword(
            @RequestBody UserUpdatePasswordDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        userService.updatePassword(userDetails.getUserId(), dto);
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }

    // 회원정보 수정 (프로필사진, 닉네임)
    @PatchMapping(path = "/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> update(
            @ModelAttribute UserUpdateRequestDto dto,
            @RequestPart(required = false) MultipartFile profileImage,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        userService.update(userDetails.getUserId(), dto, profileImage);
        return ResponseEntity.ok("회원 정보가 수정되었습니다.");
    }

    // 회원 탈퇴
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal CustomUserDetails user) {
        userService.withdraw(user.getUserId());
        return ResponseEntity.noContent().build();
    }

    // 계정 복구
    @PostMapping("/restore")
    public ResponseEntity<UserResponseDto> restore(@RequestBody UserLoginRequestDto request) {
        return ResponseEntity.ok(userService.restore(request));
    }

    // 마이페이지
    @GetMapping("/my-page")
    public ResponseEntity<UserMyPageResponseDto> getMyPage(
            @RequestParam(defaultValue = "false") boolean showAllTags,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UserMyPageResponseDto myPage = userService.getMyPageInfo(userDetails.getUserId(), showAllTags);
        return ResponseEntity.ok(myPage);
    }

}

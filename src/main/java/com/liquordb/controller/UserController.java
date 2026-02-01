package com.liquordb.controller;

import com.liquordb.dto.user.*;
import com.liquordb.entity.User;
import com.liquordb.enums.Role;
import com.liquordb.security.CustomUserDetails;
import com.liquordb.service.LiquorTagService;
import com.liquordb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
        return ResponseEntity.ok(userService.register(dto, profileImage, Role.USER));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody UserLoginRequestDto dto) {
        return ResponseEntity.ok(userService.login(dto));
    }

    // 비밀번호 찾기 - 재설정 링크 전송
    @PostMapping("/find-password")
    public ResponseEntity<String> findPassword(@RequestBody UserFindPasswordRequestDto request) {
        return ResponseEntity.ok("이메일로 재설정 링크를 전송했습니다.");
    }

    // 비밀번호 재설정
    @PatchMapping("/update-password")
    public ResponseEntity<String> updatePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                 @RequestBody UserUpdatePasswordDto dto) {
        userService.updatePassword(userDetails.getUserId(), dto);
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }

    // 회원정보 수정 (프로필사진, 닉네임)
    @PatchMapping(path = "/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> update(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         @ModelAttribute UserUpdateRequestDto dto,
                                         @RequestPart MultipartFile profileImage) {
        userService.update(userDetails.getUserId(), dto, profileImage);
        return ResponseEntity.ok("회원 정보가 수정되었습니다.");
    }

    // 회원 탈퇴
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal CustomUserDetails userDetails,
                                       @PathVariable UUID id) {
        userService.withdraw(id);
        return ResponseEntity.noContent().build();
    }

    // 마이페이지
    @GetMapping("/my-page")
    public ResponseEntity<UserMyPageResponseDto> getMyPage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "false") boolean showAllTags) {

        UserMyPageResponseDto myPage = userService.getMyPageInfo(userDetails.getUserId(), showAllTags);
        return ResponseEntity.ok(myPage);
    }

}

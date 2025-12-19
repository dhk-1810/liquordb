package com.liquordb.controller;

import com.liquordb.dto.user.*;
import com.liquordb.entity.User;
import com.liquordb.service.LiquorTagService;
import com.liquordb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * 유저 컨트롤러 클래스입니다.
 * 유저 회원가입, 로그인, 아이디 찾기, 비밀번호 찾기, 회원 탈퇴, 마이페이지 조회, 선호 태그 기반 주류 추천 기능을 지원합니다.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping
    public ResponseEntity<UserResponseDto> register(
            @RequestBody UserRegisterRequestDto dto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        return ResponseEntity.ok(userService.register(dto, profileImage, User.Role.USER));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(@RequestBody UserLoginRequestDto dto) {
        return ResponseEntity.ok(userService.login(dto));
    }

    // 비밀번호 찾기 - 재설정 링크 전송
    @PostMapping("/find-password")
    public ResponseEntity<String> findPassword(@RequestBody UserFindPasswordRequestDto request) {
        // userService.sendPasswordResetLink(request);
        return ResponseEntity.ok("이메일로 임시 비밀번호를 전송했습니다.");
    }

    // 비밀번호 재설정
    @PatchMapping("/update-password")
    public ResponseEntity<String> updatePassword(@AuthenticationPrincipal User currentUser,
                                                 @RequestBody UserUpdatePasswordDto dto) {
        userService.updatePassword(currentUser.getId(), dto);
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }

    // 회원정보 수정 (프로필사진, 닉네임)
    @PatchMapping("/update")
    public ResponseEntity<String> update(@AuthenticationPrincipal User currentUser,
                                         @ModelAttribute UserUpdateRequestDto dto,
                                         @RequestPart MultipartFile profileImage) {
        userService.update(currentUser.getId(), dto, profileImage);
        return ResponseEntity.ok("회원 정보가 수정되었습니다.");
    }

    // 회원 탈퇴
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 마이페이지
    @GetMapping("/mypage")
    public ResponseEntity<UserMyPageResponseDto> getMyPage(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "false") boolean showAllTags) {

        UserMyPageResponseDto myPage = userService.getMyPageInfo(currentUser.getId(), showAllTags);
        return ResponseEntity.ok(myPage);
    }

}

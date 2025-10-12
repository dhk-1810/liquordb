package com.liquordb.controller;

import com.liquordb.dto.liquor.LiquorSummaryDto;
import com.liquordb.dto.user.*;
import com.liquordb.entity.User;
import com.liquordb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 유저 컨트롤러 클래스입니다.
 * 유저 회원가입, 로그인, 아이디 찾기, 비밀번호 찾기, 회원 탈퇴, 마이페이지 조회, 선호 태그 기반 주류 추천 기능을 지원합니다.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register") // 회원가입
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRegisterRequestDto dto) {
        UserResponseDto response = userService.register(dto);
        return ResponseEntity.ok(response); // 상태 코드 200 OK + 본문 포함
    }

    @PostMapping("/login") // 로그인
    public ResponseEntity<UserLoginResponseDto> login(@RequestBody UserLoginRequestDto dto) {
        return ResponseEntity.ok(userService.login(dto));
    }

    @PostMapping("/find-password") // 임시 비번 전송
    public ResponseEntity<String> findPassword(@RequestBody UserFindPasswordRequestDto request) {
        userService.findPasswordAndSend(request);
        return ResponseEntity.ok("이메일로 임시 비밀번호를 전송했습니다.");
    }

    @DeleteMapping("/{id}") // 회원 탈퇴
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mypage/{userId}") // 마이페이지
    public ResponseEntity<UserMyPageResponseDto> getMyPage(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "false") boolean showAllTags) {

        UserMyPageResponseDto myPage = userService.getMyPageInfo(userId, showAllTags);

        // 프론트는 myPage.getStatus()가 WARNED이면 팝업 표시 가능
        return ResponseEntity.ok(myPage);
    }


    @PutMapping("/update") // 회원정보 수정 (프로필사진, 닉네임)
    public ResponseEntity<String> updateUser(@AuthenticationPrincipal User currentUser,
                                             @ModelAttribute UserUpdateRequestDto dto) {
        userService.updateUser(currentUser.getId(), dto);
        return ResponseEntity.ok("회원 정보가 수정되었습니다.");
    }

    @PutMapping("/update-password") // 비밀번호 재설정
    public ResponseEntity<String> updatePassword(@AuthenticationPrincipal User currentUser,
                                                 @RequestBody UserUpdatePasswordDto dto) {
        userService.updatePassword(currentUser.getId(), dto);
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }

    @GetMapping("/{userId}/preferred-liquors") // 선호 태그 기반 주류 추천
    public ResponseEntity<List<LiquorSummaryDto>> getPreferredLiquors(@PathVariable Long userId) {
        List<LiquorSummaryDto> liquors = userService.getLiquorsByUserPreferredTags(userId);
        return ResponseEntity.ok(liquors);
    }
}

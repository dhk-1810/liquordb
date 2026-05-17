package com.liquordb.controller;

import com.liquordb.dto.JwtDto;
import com.liquordb.dto.user.*;
import com.liquordb.exception.user.UserAccessDeniedException;
import com.liquordb.security.CustomUserDetails;
import com.liquordb.security.JwtInformation;
import com.liquordb.security.TokenUtil;
import com.liquordb.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
        authorizeUser(userId, user);
        UserMyPageDto myPage = userService.getMyPageInfo(userId);
        return ResponseEntity.ok(myPage);
    }

    // 회원정보 수정 (프로필사진, 닉네임)
    @PatchMapping(path = "/{userId}/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<JwtDto> update(
            @PathVariable UUID userId,
            @ModelAttribute UserUpdateRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @CookieValue(value = "REFRESH_TOKEN") String refreshToken,
            @AuthenticationPrincipal CustomUserDetails user,
            HttpServletResponse response
    ) {
        authorizeUser(userId, user);
        JwtInformation newInfo = userService.update(userId, request, profileImage, refreshToken);
        if (newInfo.refreshToken() != null) {
            Cookie refreshCookie = TokenUtil.createRefreshTokenCookie(newInfo.refreshToken());
            response.addCookie(refreshCookie);
        }
        return ResponseEntity.ok(new JwtDto(newInfo.dto(), newInfo.accessToken()));
    }

    // 비밀번호 수정 (로그인 상태에서)
    @PatchMapping("/{userId}/update-password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable UUID userId,
            @RequestBody @Valid PasswordUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        authorizeUser(userId, user);
        userService.updatePassword(userId, request);
        return ResponseEntity.noContent().build();
    }

    // 회원 탈퇴
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID userId,
            @RequestHeader("Authorization") String authHeader,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        authorizeUser(userId, user);
        String accessToken = authHeader.substring(7);
        userService.withdraw(userId, accessToken);
        return ResponseEntity.noContent().build();
    }

    private void authorizeUser(UUID userId, CustomUserDetails user) {
        if (!user.id().equals(userId)) {
            throw new UserAccessDeniedException(user.id());
        }
    }
}

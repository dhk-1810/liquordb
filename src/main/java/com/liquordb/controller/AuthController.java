package com.liquordb.controller;

import com.liquordb.dto.JwtDto;
import com.liquordb.dto.user.*;
import com.liquordb.enums.Role;
import com.liquordb.security.JwtInformation;
import com.liquordb.security.TokenUtil;
import com.liquordb.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UserResponseDto> register(
            @RequestBody SignUpRequestDto dto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        return ResponseEntity.ok(authService.signUp(dto, profileImage, Role.USER));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<JwtInformation> login(@RequestBody @Valid LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // 토큰 재발급
    @PostMapping("/token-refresh")
    public ResponseEntity<JwtDto> refreshToken(
            @CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken,
            HttpServletResponse response){

        JwtInformation newInfo = authService.refresh(refreshToken);
        Cookie refreshCookie = TokenUtil.createRefreshTokenCookie(newInfo.refreshToken());
        response.addCookie(refreshCookie);
        JwtDto jwtDto = JwtDto.builder()
                .accessToken(newInfo.accessToken())
                .userDto(newInfo.dto())
                .build();

        return ResponseEntity.ok(jwtDto);
    }

    // 비밀번호 재설정 링크 전송
    @PostMapping("/find-password")
    public ResponseEntity<Void> sendPasswordResetEmail(@RequestBody @Valid PasswordFindRequestDto request) {
        authService.sendPasswordResetLink(request);
        return ResponseEntity.noContent().build();
    }

    // 비밀번호 초기화
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid PasswordResetRequest request){
        authService.resetPasswordByToken(request);
        return ResponseEntity.noContent().build();
    }

    // 계정 복구
    @PostMapping("/restore")
    public ResponseEntity<UserResponseDto> restore(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.restore(request));
    }
}

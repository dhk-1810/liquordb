package com.liquordb.controller;

import com.liquordb.dto.user.*;
import com.liquordb.enums.Role;
import com.liquordb.service.AuthService;
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
    // TODO 시큐리티로 대체
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody @Valid LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
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
    // TODO 시큐리티로 대체
    @PostMapping("/restore")
    public ResponseEntity<UserResponseDto> restore(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.restore(request));
    }
}

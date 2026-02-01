package com.liquordb.controller;

import com.liquordb.dto.user.UserRegisterRequestDto;
import com.liquordb.dto.user.UserResponseDto;
import com.liquordb.enums.Role;
import com.liquordb.enums.UserStatus;
import com.liquordb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    // 관리자 회원가입
    @PostMapping
    public ResponseEntity<UserResponseDto> createAdmin(@RequestBody UserRegisterRequestDto dto,
                                                       @RequestPart(required = false) MultipartFile profileImage
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.signUp(dto, profileImage, Role.ADMIN));
    }

    // 유저 전체 조회 및 검색
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> userList(@RequestParam(required = false) String keyword, // 닉네임 또는 이메일 검색어
                                                          @RequestParam(required = false) UserStatus status) {
        List<UserResponseDto> users = userService.getUsers(keyword, status);
        return ResponseEntity.ok(users);
    }

    // 유저 이용제한 처리
    // TODO 검토
    @PostMapping("/{userId}/restrict")
    public ResponseEntity<UserResponseDto> restrictUser(@PathVariable UUID userId,
                                                        @RequestParam String period) {
        UserResponseDto user = userService.restrictUser(userId, period);
        return ResponseEntity.ok(user);
    }
}

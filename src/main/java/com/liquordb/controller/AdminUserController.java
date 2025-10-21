package com.liquordb.controller;

import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.dto.user.UserRegisterRequestDto;
import com.liquordb.dto.user.UserResponseDto;
import com.liquordb.entity.User;
import com.liquordb.entity.UserStatus;
import com.liquordb.repository.ReviewRepository;
import com.liquordb.service.CommentService;
import com.liquordb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 관리자용 유저 컨트롤러 클래스입니다.
 */
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    // 관리자 회원가입
    @PostMapping
    public ResponseEntity<UserResponseDto> createAdmin(@RequestBody UserRegisterRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.register(dto, User.Role.ADMIN));
    }

    // 유저 전체 조회 및 검색
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> userList(@RequestParam(required = false) String keyword, // 닉네임 또는 이메일 검색어
                                                          @RequestParam(required = false) UserStatus status) {
        List<UserResponseDto> users = userService.searchUsers(keyword, status);
        return ResponseEntity.ok(users);
    }

    // 유저 이용제한 처리
    @PostMapping("/{userId}/restrict")
    public ResponseEntity<UserResponseDto> restrictUser(@PathVariable UUID userId,
                               @RequestParam String period) {
        UserResponseDto user = userService.restrictUser(userId, period);
        return ResponseEntity.ok(user);
    }
}

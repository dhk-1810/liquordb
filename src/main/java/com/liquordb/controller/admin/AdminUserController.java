package com.liquordb.controller.admin;

import com.liquordb.dto.user.UserResponseDto;
import com.liquordb.enums.UserStatus;
import com.liquordb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    // TODO 사용자 권한변경

    // 유저 전체 조회 및 검색
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> userList(
            @RequestParam(required = false) String keyword, // 닉네임 또는 이메일 검색어
            @RequestParam(required = false) UserStatus status
    ) {
        List<UserResponseDto> users = userService.getUsers(keyword, status);
        return ResponseEntity.ok(users);
    }

}

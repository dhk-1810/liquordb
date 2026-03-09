package com.liquordb.controller.admin;

import com.liquordb.dto.PageResponse;
import com.liquordb.dto.user.UserListGetRequest;
import com.liquordb.dto.user.UserResponseDto;
import com.liquordb.enums.Role;
import com.liquordb.security.JwtInformation;
import com.liquordb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    // 사용자 권한 변경
    @PatchMapping("/{userId}/role")
    public ResponseEntity<JwtInformation> updateRole(
            @PathVariable UUID userId,
            @RequestParam Role role,
            @CookieValue(value = "REFRESH_TOKEN") String refreshToken
    ) {
        JwtInformation response = userService.updateRole(role, userId, refreshToken);
        return ResponseEntity.ok(response);
    }

    // 사용자 전체 조회 및 검색
    @GetMapping
    public ResponseEntity<PageResponse<UserResponseDto>> userList(
            @ModelAttribute UserListGetRequest request
    ) {
        PageResponse<UserResponseDto> users = userService.getAll(request);
        return ResponseEntity.ok(users);
    }

}

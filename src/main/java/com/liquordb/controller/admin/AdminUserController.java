package com.liquordb.controller.admin;

import com.liquordb.dto.PageResponse;
import com.liquordb.dto.user.UserListGetRequest;
import com.liquordb.dto.user.UserResponseDto;
import com.liquordb.enums.Role;
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
    @PatchMapping("/role")
    public ResponseEntity<Void> updateRole(Role role, UUID userId){
        userService.updateRole(role, userId);
        return ResponseEntity.noContent().build();
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

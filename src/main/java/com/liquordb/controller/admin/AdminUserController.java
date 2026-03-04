package com.liquordb.controller.admin;

import com.liquordb.dto.PageResponse;
import com.liquordb.dto.user.UserListGetRequest;
import com.liquordb.dto.user.UserResponseDto;
import com.liquordb.enums.UserStatus;
import com.liquordb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    // TODO 사용자 권한변경

    // 유저 전체 조회 및 검색
    @GetMapping
    public ResponseEntity<PageResponse<UserResponseDto>> userList(
            @ModelAttribute UserListGetRequest request
    ) {
        PageResponse<UserResponseDto> users = userService.getAll(request);
        return ResponseEntity.ok(users);
    }

}

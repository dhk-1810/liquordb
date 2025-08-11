package com.liquordb.user.controller;

import com.liquordb.review.dto.CommentResponseDto;
import com.liquordb.review.dto.ReviewResponseDto;
import com.liquordb.user.dto.UserAdminDto;
import com.liquordb.user.entity.UserStatus;
import com.liquordb.user.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 관리자용 유저 컨트롤러 클래스입니다.
 */
@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    // 유저 전체 조회 및 검색
    @GetMapping
    public String userList(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) UserStatus status,
                           Model model) {
        List<UserAdminDto> users = adminUserService.searchUsers(keyword, status);
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "admin/user-list";
    }

    // 유저별 리뷰 조회
    @GetMapping("/{userId}/reviews")
    public String userReviews(@PathVariable Long userId, Model model) {
        List<ReviewResponseDto> reviews = adminUserService.getUserReviews(userId);
        model.addAttribute("reviews", reviews);
        return "admin/user-reviews";
    }

    // 유저별 댓글 조회
    @GetMapping("/{userId}/comments")
    public String userComments(@PathVariable Long userId, Model model) {
        List<CommentResponseDto> comments = adminUserService.getUserComments(userId);
        model.addAttribute("comments", comments);
        return "admin/user-comments";
    }

    // 유저 이용제한 처리
    @PostMapping("/{userId}/restrict")
    public String restrictUser(@PathVariable Long userId,
                               @RequestParam String period) {
        adminUserService.restrictUser(userId, period);
        return "redirect:/admin/users";
    }
}

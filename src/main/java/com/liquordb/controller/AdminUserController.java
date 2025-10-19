package com.liquordb.controller;

import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.dto.review.ReviewResponseDto;
import com.liquordb.dto.user.UserResponseDto;
import com.liquordb.entity.UserStatus;
import com.liquordb.repository.ReviewRepository;
import com.liquordb.service.CommentService;
import com.liquordb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final ReviewRepository reviewRepository;
    private final CommentService commentService;

    // 유저 전체 조회 및 검색
    @GetMapping
    public String userList(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) UserStatus status,
                           Model model) {
        List<UserResponseDto> users = userService.searchUsers(keyword, status);
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "admin/user-list";
    }

    // 유저별 리뷰 조회
    @GetMapping("/{userId}/reviews")
    public String userReviews(@PathVariable UUID userId, Model model) {
        Page<ReviewResponseDto> reviews = reviewRepository.findAllByUserIdAndIsHiddenFalse(userId, pageable);
        model.addAttribute("reviews", reviews);
        return "admin/user-reviews";
    }

    // 유저별 댓글 조회
    @GetMapping("/{userId}/comments")
    public String userComments(@PathVariable UUID userId, Model model) {
        List<CommentResponseDto> comments = commentService.findByUserId(userId, pageable);
        model.addAttribute("comments", comments);
        return "admin/user-comments";
    }

    // 유저 이용제한 처리
    @PostMapping("/{userId}/restrict")
    public String restrictUser(@PathVariable UUID userId,
                               @RequestParam String period) {
        userService.restrictUser(userId, period);
        return "redirect:/admin/users";
    }
}

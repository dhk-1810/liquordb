package com.liquordb.controller;

import com.liquordb.PageResponse;
import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.entity.Comment;
import com.liquordb.entity.Review;
import com.liquordb.repository.CommentRepository;
import com.liquordb.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;

    // 댓글 조회 - 작성 유저, 댓글 상태 필터링 가능
    @GetMapping("/{userId}")
    public ResponseEntity<PageResponse<CommentResponseDto>> findByUserIdAndCommentStatus(
            @PathVariable UUID userId,
            @RequestParam(required = false) Comment.CommentStatus status,
            Pageable pageable
    ) {
        PageResponse<CommentResponseDto> comments = commentService.findAllByOptionalFilters(userId, status, pageable);
        return ResponseEntity.ok(comments);
    }

}

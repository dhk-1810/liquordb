package com.liquordb.controller.admin;

import com.liquordb.dto.PageResponse;
import com.liquordb.dto.comment.CommentResponseDto;
import com.liquordb.dto.comment.request.CommentSearchRequest;
import com.liquordb.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;

    // 댓글 조회 - 작성 유저, 댓글 상태 필터링 가능
    @GetMapping
    public ResponseEntity<PageResponse<CommentResponseDto>> getByUsernameAndCommentStatus(
            @ModelAttribute CommentSearchRequest request
    ) {
        PageResponse<CommentResponseDto> comments = commentService.getAll(request);
        return ResponseEntity.ok(comments);
    }

}

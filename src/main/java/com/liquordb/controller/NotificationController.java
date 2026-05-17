package com.liquordb.controller;

import com.liquordb.dto.NotificationListGetRequest;
import com.liquordb.dto.NotificationResponseDto;
import com.liquordb.security.CustomUserDetails;
import com.liquordb.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> get(
            @ModelAttribute @Valid NotificationListGetRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.ok(notificationService.get(request, user.id()));
    }

    @PatchMapping("/{notificationId}")
    public ResponseEntity<Void> read(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        notificationService.read(notificationId, user.id());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        notificationService.delete(notificationId, user.id());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(@AuthenticationPrincipal CustomUserDetails user){
        notificationService.clear(user.id());
        return ResponseEntity.noContent().build();
    }
}

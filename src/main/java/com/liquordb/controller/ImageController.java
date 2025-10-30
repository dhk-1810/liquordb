package com.liquordb.controller;

import com.liquordb.entity.User;
import com.liquordb.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @DeleteMapping("/{reviewId}/images/{imageId}")
    public ResponseEntity<Void> deleteReviewImage(
            @PathVariable Long reviewId,
            @PathVariable Long imageId,
            @AuthenticationPrincipal User user
    ) {
        imageService.deleteReviewImage(reviewId, imageId, user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/images/{imageId}")
    public ResponseEntity<Void> deleteProfileImage(
            @PathVariable Long reviewId,
            @PathVariable Long imageId,
            @AuthenticationPrincipal User user
    ) {
        imageService.deleteReviewImage(reviewId, imageId, user);
        return ResponseEntity.noContent().build();
    }
}

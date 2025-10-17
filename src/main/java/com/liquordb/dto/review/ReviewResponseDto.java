package com.liquordb.dto.review;

import com.liquordb.entity.Review;
import com.liquordb.entity.ReviewImage;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDto {
    private Long id;
    private UUID userId;
    private Long liquorId;
    private Double rating;
    private String title;
    private String content;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // DTO 변환
    public static ReviewResponseDto from(Review review) {
        List<String> imageUrls = review.getImages() != null
                ? review.getImages().stream()
                .map(ReviewImage::getImageUrl)
                .toList()
                : Collections.emptyList();

        return ReviewResponseDto.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .liquorId(review.getLiquor().getId())
                .rating(review.getRating())
                .title(review.getTitle())
                .content(review.getContent())
                .imageUrls(imageUrls)
                .createdAt(review.getCreatedAt())
                .build();
    }
}

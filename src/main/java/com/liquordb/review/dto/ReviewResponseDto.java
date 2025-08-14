package com.liquordb.review.dto;

import com.liquordb.review.entity.Review;
import com.liquordb.review.entity.ReviewImage;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDto {
    private Long id;
    private Long userId;
    private Long liquorId;
    private Double rating;
    private String title;
    private String content;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Entity 객체를 DTO 객체로 변환해주는 함수
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

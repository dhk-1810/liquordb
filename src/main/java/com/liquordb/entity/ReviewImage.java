package com.liquordb.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 리뷰에 첨부되는 사진 엔터티입니다.
 * 리뷰-이미지의 1:N 관계를 표현하며,
 * 사진은 0장 이상 5장 이하 업로드 가능합니다.
 */
@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    private String imageUrl;
}

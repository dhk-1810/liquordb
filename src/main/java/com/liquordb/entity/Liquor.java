package com.liquordb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "liquors")
public class Liquor extends LikeableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    private LiquorCategory category; // 주종 대분류

    @ManyToOne
    @JoinColumn(name = "category_id")
    private LiquorSubcategory subcategory; // 주종 소분류

    private String country; // 제조국
    private String manufacturer; // 제조사
    private Double abv; // 도수 (Alcohol by Volume)
    private Double averageRating; // 리뷰 평균 점수

    @Column(nullable = false)
    private boolean isDiscontinued; // 단종 여부

    private String imageUrl; // 대표 이미지 사진 저장경로

    private long reviewCount = 0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // 주종 대분류
    public enum LiquorCategory {
        BEER, WINE, WHISKY, OTHER
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (averageRating == null) averageRating = 0.0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Builder
    private Liquor(String name, LiquorCategory category, LiquorSubcategory subcategory,
                   String country, String manufacturer, Double abv, String imageUrl){
        this.name = name;
        this.category = category;
        this.subcategory = subcategory;
        this.country = country;
        this.manufacturer = manufacturer;
        this.abv = abv;
        this.imageUrl = imageUrl; // TODO 개선필요
    }

    public static Liquor create(String name, LiquorCategory category, LiquorSubcategory subcategory,
                                String country, String manufacturer, Double abv, String imageUrl) {
        return Liquor.builder()
                .name(name)
                .category(category)
                .subcategory(subcategory)
                .country(country)
                .manufacturer(manufacturer)
                .abv(abv)
                .imageUrl(imageUrl)
                .build();
    }

    public void updateFromDto(Boolean isDiscontinued, Boolean deleteImage) {
        if (isDiscontinued == null) this.isDiscontinued = false;
        if (deleteImage == null) imageUrl = null; // TODO 개선필요
    }

    public void softDelete(LocalDateTime deletedAt) {
        if (this.isDeleted) return;
        this.isDeleted = true;
        this.deletedAt = deletedAt;
        // 연관 리뷰 삭제는 서비스단에서 수행.
        // 리뷰 좋아요는 soft delete 대신 조회만 안되게 처리.
    }

    public void restore() {
        if (!this.isDeleted) return;
        this.isDeleted = false;
        this.deletedAt = null;
        // 연관 리뷰 복구는 서비스단에서 수행.
    }

    public void increaseReviewCount() {
        this.reviewCount++;
    }

    public void decreaseReviewCount() {
        if (this.reviewCount <= 0) return;
        this.reviewCount--;
    }

}
package com.liquordb.liquor.entity;

import com.liquordb.like.entity.LiquorLike;
import com.liquordb.liquor.dto.LiquorRequestDto;
import com.liquordb.review.entity.Review;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "liquor")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Liquor {

    @Id
    @Column(name = "liquor_id")
    private Long id;

    @Column(nullable = false)
    private boolean isHidden = false; // 숨기기 여부

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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "liquor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LiquorLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "liquor", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "liquor")
    private Set<LiquorTag> liquorTags = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (averageRating == null) averageRating = 0.0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public List<String> getTags() {
        if (liquorTags == null) return Collections.emptyList();
        return liquorTags.stream()
                .map(lt -> lt.getTag().getName())  // Tag의 name 필드
                .collect(Collectors.toList());
    }

    public void updateFromDto(LiquorRequestDto dto) {
        this.name = dto.getName();
        this.category = dto.getCategory();
        this.subcategory = dto.getSubcategory();
        this.imageUrl = dto.getImageUrl();
    }
}
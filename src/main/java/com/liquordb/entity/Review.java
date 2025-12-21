package com.liquordb.entity;

import com.liquordb.dto.review.ReviewRequestDto;
import com.liquordb.dto.review.ReviewUpdateRequestDto;
import com.liquordb.entity.reviewdetail.ReviewDetail;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double rating;

    private String title;

    @Lob
    private String content;

    @Column(nullable = false)
    private ReviewStatus status = ReviewStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "liquor_id")
    private Liquor liquor;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReviewLike> likes = new HashSet<>();

    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL)
    private ReviewDetail detail;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> images;

    @OneToMany(mappedBy = "review")
    private List<Report> reports = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime hiddenAt;
    private LocalDateTime deletedAt;

    public enum ReviewStatus {
        ACTIVE, HIDDEN, DELETED
    }

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void update(ReviewUpdateRequestDto request) {
        if (request.rating() != null) {
            this.rating = request.rating();
        }
        if (request.title() != null) {
            this.title = request.title();
        }
        if (request.content() != null) {
            this.content = request.content();
        }
    }

    public void hide(LocalDateTime hiddenAt) {
        if (this.status == ReviewStatus.HIDDEN) return;
        this.status = ReviewStatus.HIDDEN;
        this.hiddenAt = hiddenAt;
    }

    public void unhide(){
        if (this.status != ReviewStatus.HIDDEN) return;
        this.status = ReviewStatus.ACTIVE;
        this.hiddenAt = null;
    }

    public void softDelete(LocalDateTime deletedAt) {
        if (this.status == ReviewStatus.DELETED) return;
        this.status = ReviewStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
        // 연관 댓글 삭제는 서비스단에서 수행.
    }

    public void restore(){
        if (this.status != ReviewStatus.DELETED) return;
        this.status = ReviewStatus.ACTIVE;
        this.deletedAt = null;
        // 연관 댓글 복구는 서비스단에서 수행.
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Review (Double rating, String title, String content,
                    User user, Liquor liquor, ReviewDetail detail
    ){
        this.rating = rating;
        this.title = title;
        this.content = content;
        this.user = user;
        this.liquor = liquor;
        this.detail = detail;
    }

    public static Review create(ReviewRequestDto request, User user, Liquor liquor) {
        return Review.builder()
                .rating(request.rating())
                .title(request.title())
                .content(request.content())
                .user(user)
                .liquor(liquor)
                .detail(request.reviewDetailRequest())
                .build();
    }
}

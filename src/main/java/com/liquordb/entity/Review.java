package com.liquordb.entity;

import com.liquordb.dto.review.ReviewRequest;
import com.liquordb.dto.review.ReviewUpdateRequest;
import com.liquordb.entity.reviewdetail.ReviewDetail;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "reviews",
        indexes = { @Index(name = "idx_rating_id", columnList = "liquorId, rating, id") }
)
public class Review extends LikeableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReviewStatus status = ReviewStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "liquor_id")
    private Liquor liquor;

    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private ReviewDetail detail;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewTag> reviewTags;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImageKey> imageKeys;

    @Column(nullable = false)
    private long commentCount;

    @Column(nullable = false)
    private long likeCount;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = true)
    private LocalDateTime updatedAt;

    @Column(nullable = true)
    private LocalDateTime deletedAt;

    public enum ReviewStatus {
        ACTIVE, HIDDEN, DELETED
    }

    @Builder
    private Review (Integer rating, String title, String content,
                    User user, Liquor liquor, ReviewDetail detail
    ){
        this.rating = rating;
        this.title = title;
        this.content = content;
        this.user = user;
        this.liquor = liquor;
        this.detail = detail;
        this.likeCount = 0;
        this.commentCount = 0;
    }

    public static Review create(ReviewRequest request, Liquor liquor, User user) {
        return Review.builder()
                .rating(request.rating())
                .title(request.title())
                .content(request.content())
                .user(user)
                .liquor(liquor)
                .build();
    }

    public void addDetail(ReviewDetail detail) {
        this.detail = detail;
        detail.setReview(this);
    }

    public void update(ReviewUpdateRequest request) {
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

    public void softDelete(LocalDateTime deletedAt) {
        if (this.status == ReviewStatus.DELETED) return;
        this.status = ReviewStatus.DELETED;
        this.deletedAt = deletedAt;
        // 연관 댓글 삭제는 서비스단에서 수행.
    }

    public void restore(){
        if (this.status != ReviewStatus.DELETED) return;
        this.status = ReviewStatus.ACTIVE;
        this.deletedAt = null;
        // 연관 댓글 복구는 서비스단에서 수행.
    }

}

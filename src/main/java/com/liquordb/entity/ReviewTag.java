package com.liquordb.entity;

import com.liquordb.entity.id.ReviewTagId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

/**
 * 주류-태그 간 다대다 관계 처리 위한 클래스
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "review_tags")
public class ReviewTag {

    @EmbeddedId
    private ReviewTagId id;

    @MapsId("reviewId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @MapsId("tagId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @CreatedDate
    private LocalDateTime createdAt;

    private ReviewTag(Review review, Tag tag) {
        this.id = new ReviewTagId(review.getId(), tag.getId());
        this.review = review;
        this.tag = tag;
    }

    public static ReviewTag create(Review review, Tag tag) {
        return new ReviewTag(review, tag);
    }

}



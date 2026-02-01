package com.liquordb.entity.reviewdetail;

import com.liquordb.entity.Review;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Review 엔터티에서 사용되는 추상 클래스입니다.
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 부모, 자식 클래스를 하나의 테이블로 관리
@DiscriminatorColumn(name = "liquor_type")
public abstract class ReviewDetail {

    @Id
    private Long id;

    @OneToOne
    @MapsId // Review 엔터티와 동일한 PK 사용
    @JoinColumn(name = "review_id")
    private Review review;

    public void setReview(Review review) {
        this.review = review;
    }
}

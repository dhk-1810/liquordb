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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 부모, 자식 클래스를 하나의 테이블로 관리
@DiscriminatorColumn(name = "liquor_type")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class ReviewDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "review_id")
    private Review review;
}

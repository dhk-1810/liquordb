package com.liquordb.review.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "liquor_type")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class ReviewDetail { // 추상 클래스

    @Id
    @Column
    private Long reviewId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "reviewId")
    private Review review;
}
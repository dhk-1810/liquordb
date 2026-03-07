package com.liquordb.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 복합 키 정의 클래스
 */
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ReviewTagId implements Serializable {

    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "tag_id")
    private Long tagId;
}
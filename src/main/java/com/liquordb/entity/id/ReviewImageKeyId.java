package com.liquordb.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class ReviewImageKeyId implements Serializable {

    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "image_key")
    private String imageKey;
}

package com.liquordb.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ReviewLikeId implements Serializable {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "review_id")
    private Long reviewId;
}


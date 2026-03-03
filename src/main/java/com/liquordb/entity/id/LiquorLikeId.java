package com.liquordb.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LiquorLikeId {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "liquor_id")
    private Long liquorId;
}
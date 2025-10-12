package com.liquordb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 주류-태그 간 다대다 관계 처리 위한 클래스입니다.
 */

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(LiquorTag.class)
public class LiquorTag {

    @Id
    @ManyToOne
    @JoinColumn(name = "liquor_id")
    private Liquor liquor;

    @Id
    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;


    @EmbeddedId
    private LiquorTagId id;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}



package com.liquordb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 주류-태그 간 다대다 관계 처리 위한 클래스
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "liquor_tags")
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

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private LiquorTag(Liquor liquor, Tag tag) {
        this.liquor = liquor;
        this.tag = tag;
    }

    public static LiquorTag create(Liquor liquor, Tag tag) {
        return LiquorTag.builder()
                .liquor(liquor)
                .tag(tag)
                .build();
    }

}



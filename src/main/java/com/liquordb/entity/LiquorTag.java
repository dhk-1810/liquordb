package com.liquordb.entity;

import com.liquordb.entity.id.LiquorTagId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 주류-태그 간 다대다 관계 처리 위한 클래스
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "liquor_tags")
public class LiquorTag {

    @EmbeddedId
    private LiquorTagId id;

    @MapsId("liquorId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liquor_id")
    private Liquor liquor;

    @MapsId("tagId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    private LiquorTag(Liquor liquor, Tag tag) {
        this.id = new LiquorTagId(liquor.getId(), tag.getId());
        this.liquor = liquor;
        this.tag = tag;
    }

    public static LiquorTag create(Liquor liquor, Tag tag) {
        return new LiquorTag(liquor, tag);
    }

}



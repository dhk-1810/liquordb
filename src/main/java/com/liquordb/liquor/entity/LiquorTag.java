package com.liquordb.liquor.entity;

import com.liquordb.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.*;

/**
 * 주류-태그 간 다대다 관계 처리 위한 클래스입니다.
 */

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(LiquorTagId.class)
public class LiquorTag {

    @Id
    @ManyToOne
    @JoinColumn(name = "liquor_id")
    private Liquor liquor;

    @Id
    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;
}



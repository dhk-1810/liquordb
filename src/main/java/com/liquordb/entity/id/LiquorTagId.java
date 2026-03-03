package com.liquordb.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

/**
 * 복합 키 정의 클래스
 */
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LiquorTagId implements Serializable {

    @Column(name = "liquor_id")
    private Long liquorId;

    @Column(name = "tag_id")
    private Long tagId;
}
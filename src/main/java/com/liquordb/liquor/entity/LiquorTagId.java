package com.liquordb.liquor.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * LiquorTag에서 사용하는 복합 키 정의 클래스입니다.
 */

@Data
@Embeddable
public class LiquorTagId implements Serializable {
    private Long liquor;
    private Long tag;

    public LiquorTagId() {}

    public LiquorTagId(Long liquor, Long tag) {
        this.liquor = liquor;
        this.tag = tag;
    }

    // JPA에서 복합키 클래스는 equals()와 hashCode()를 꼭 직접 구현해야 함.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LiquorTagId)) return false;
        LiquorTagId that = (LiquorTagId) o;
        return Objects.equals(liquor, that.liquor) &&
                Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(liquor, tag);
    }
}
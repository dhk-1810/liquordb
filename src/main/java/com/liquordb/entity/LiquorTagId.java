package com.liquordb.entity;

import lombok.*;

import java.io.Serializable;

/**
 * LiquorTag에서 사용하는 복합 키 정의 클래스입니다.
 */

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LiquorTagId implements Serializable {
    private Long liquorId;
    private Long tagId;

    // JPA에서 복합키 클래스는 equals()와 hashCode()를 꼭 직접 구현해야 함.

    /*
    @EqualsAndHashCode로 대체

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
     */
}
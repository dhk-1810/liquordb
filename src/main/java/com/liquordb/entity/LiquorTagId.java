package com.liquordb.entity;

import lombok.*;

import java.io.Serializable;

/**
 * 복합 키 정의 클래스
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LiquorTagId implements Serializable {

    private Long liquorId;
    private Long tagId;
}
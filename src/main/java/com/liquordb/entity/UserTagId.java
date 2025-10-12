package com.liquordb.entity;

import lombok.*;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserTagId implements Serializable {
    private Long user;
    private Long tag;
}

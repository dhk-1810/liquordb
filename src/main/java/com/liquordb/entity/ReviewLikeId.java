package com.liquordb.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ReviewLikeId implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID user;
    private Long review;
}


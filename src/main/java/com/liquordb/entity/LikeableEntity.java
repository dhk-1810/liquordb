package com.liquordb.entity;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class LikeableEntity {

    private long likeCount = 0;

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (likeCount <= 0) return;
        this.likeCount--;
    }

    public long getLikeCount() {
        return likeCount;
    }
}

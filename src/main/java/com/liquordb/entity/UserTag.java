package com.liquordb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 유저-태그 간 다대다 관계 처리 위한 엔터티
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_tags")
@IdClass(UserTagId.class)
public class UserTag {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private UserTag(User user, Tag tag) {
        this.user = user;
        this.tag = tag;
    }

    public static UserTag create(User requestUser, Tag tag) {
        return UserTag.builder()
                .user(requestUser)
                .tag(tag)
                .build();
    }

}

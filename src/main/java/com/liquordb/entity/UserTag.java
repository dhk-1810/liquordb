package com.liquordb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 유저-태그 간 다대다 관계 처리 위한 클래스입니다.
 */

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(UserTagId.class)
public class UserTag {

    @EmbeddedId
    private UserTagId id;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

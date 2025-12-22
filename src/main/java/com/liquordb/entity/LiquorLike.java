package com.liquordb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "liquor_likes")
public class LiquorLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "liquor_id")
    private Liquor liquor;

    @Column(nullable = false)
    private boolean liked;

    @Column(nullable = false, updatable = false)
    private LocalDateTime likedAt;

    @PrePersist
    public void onCreate() {
        likedAt = LocalDateTime.now();
    }

    @Builder(access = AccessLevel.PRIVATE)
    public LiquorLike(User user, Liquor liquor) {
        this.user = user;
        this.liquor = liquor;
    }

    public static LiquorLike create(User user, Liquor liquor) {
        return LiquorLike.builder()
                .user(user)
                .liquor(liquor)
                .build();
    }
}

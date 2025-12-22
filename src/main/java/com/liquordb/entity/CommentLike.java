package com.liquordb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment_likes")
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime likedAt;

    @PrePersist
    public void onCreate() {
        likedAt = LocalDateTime.now();
    }

    @Builder(access = AccessLevel.PRIVATE)
    public CommentLike(User user, Comment comment, LocalDateTime likedAt) {
        this.user = user;
        this.comment = comment;
        this.likedAt = likedAt;
    }

    public static CommentLike create(User user, Comment comment) {
        return CommentLike.builder()
                .user(user)
                .comment(comment)
                .build();
    }
}
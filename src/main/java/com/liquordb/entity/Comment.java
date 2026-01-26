package com.liquordb.entity;

import com.liquordb.dto.comment.CommentUpdateRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CommentStatus status = CommentStatus.ACTIVE; // 기본값은 ACTIVE

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id") // 대댓글 기능. self-referencing
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Comment> replies = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private long likeCount = 0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime hiddenAt;
    private LocalDateTime deletedAt;

    public enum CommentStatus {
        ACTIVE,
        HIDDEN, // 신고 누적시 자동 숨김처리. 관리자 복구 전까지 유효.
        DELETED // 작성자 스스로 삭제
    }

    @PrePersist // JPA(EntityManager)가 엔티티를 DB에 처음 저장(Persist=영속화)하기 바로 직전에 자동 호출
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() { updatedAt = LocalDateTime.now(); }

    @Builder(access = AccessLevel.PRIVATE)
    public Comment(String content, Review review, Comment parent, User user){
        this.content = content;
        this.review = review;
        this.parent = parent;
        this.user = user;
    }

    public static Comment create(String content, Review review, Comment parent, User user) {
        return Comment.builder()
                .content(content)
                .review(review)
                .parent(parent)
                .user(user)
                .build();
    }

    public void update(CommentUpdateRequestDto request) {
        this.content = request.content();
    }

    public void hide(LocalDateTime deletedAt) {
        if (this.status == CommentStatus.HIDDEN) return;
        this.status = CommentStatus.HIDDEN;
        this.hiddenAt = deletedAt;
    }

    public void unhide(){
        if (this.status != CommentStatus.HIDDEN) return;
        this.status = CommentStatus.ACTIVE;
        this.hiddenAt = null;
    }

    public void softDelete(LocalDateTime deletedAt) {
        if (this.status == CommentStatus.DELETED) return;
        this.status = CommentStatus.DELETED;
        this.deletedAt = deletedAt;
    }

    public void restore(){
        if (this.status != CommentStatus.DELETED) return;
        this.status = CommentStatus.ACTIVE;
        this.deletedAt = null;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (likeCount <= 0) return;
        this.likeCount--;
    }
}
package com.liquordb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(nullable = false)
//    private boolean isHidden = false;
//
//    @Column(nullable = false)
//    private boolean isDeleted = false;

    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CommentStatus status = CommentStatus.ACTIVE; // 기본값은 ACTIVE

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne
    @JoinColumn(name = "parent_id") // 대댓글 기능 구현. self-referencing
    private Comment parent;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL) // 대댓글
    private List<Comment> replies = new ArrayList<>();

    @OneToMany(mappedBy = "comment")
    private List<Report> reports = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

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
}
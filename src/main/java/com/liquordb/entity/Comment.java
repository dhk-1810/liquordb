package com.liquordb.entity;

import com.liquordb.dto.comment.request.CommentUpdateRequest;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments")
public class Comment extends LikeableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CommentStatus status;

    @Column(nullable = false)
    private long likeCount;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id") // self-referencing
    private Comment parent;

    // 부모 댓글 삭제 시 자식 댓글들은 남겨둠
//    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
//    private List<Comment> replies = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    public enum CommentStatus {
        ACTIVE,
        HIDDEN, // 신고 누적시 자동 숨김처리. 관리자 복구 전까지 유효.
        DELETED // 작성자 스스로 삭제
    }

    public Comment(String content, Review review, Comment parent, User user){
        this.content = content;
        this.review = review;
        this.parent = parent;
        this.user = user;
        this.status = CommentStatus.ACTIVE;
        likeCount = 0;
    }

    public static Comment create(String content, Review review, Comment parent, User user) {
        return new Comment(content, review, parent, user);
    }

    public void update(CommentUpdateRequest request) {
        this.content = request.content();
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
package com.liquordb.review.entity;

import com.liquordb.like.entity.Like;
import com.liquordb.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    private Long id;

    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    @Column(nullable = false)
    private boolean isHidden = false;

    @ManyToOne
    @JoinColumn(name = "parent_id") // 대댓글 기능 구현. self-referencing
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL) // 대댓글
    private List<Comment> replies;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

//    @OneToMany(mappedBy = "liquor", cascade = CascadeType.ALL)
//    private List<Like> likes;

    @PrePersist // JPA(EntityManager)가 엔티티를 DB에 저장(Persist)하기 "바로 직전에 자동 호출되는 메서드"에 붙이는 어노테이션.
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void onUpdate() { updatedAt = LocalDateTime.now(); }
}

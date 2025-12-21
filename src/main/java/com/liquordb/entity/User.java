package com.liquordb.entity;

import com.liquordb.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "user")
@Getter @Setter // Setter 대체 필요
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    @Email
    private String email; // 로그인은 이메일로만 가능.

    @Column(nullable = false, unique = true, length = 30)
    private String nickname;

    @Column(nullable = false, length = 100)
    private String password; // 소셜 로그인시에도 회원정보 수정을 위해 비밀번호 설정은 필요.

    private String socialProvider; // 소셜로그인 제공자 (google, kakao 등)

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private int reportCount; // 신고된 건수

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private File profileImage; // TODO S3에 저장

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LiquorLike> liquorLikes = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReviewLike> reviewLikes = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentLike> commentLikes = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserTag> userTags = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<Notice> notice = new ArrayList<>();

    // 활동 제한 해제 일시
    @Column
    private LocalDateTime suspendedUntil;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime withdrawnAt;

    public enum Role {
        USER, ADMIN // 유저 계정, 관리자 계정
    }

    // 유저 활동 제한 (댓글, 리뷰 작성)
    public void suspend() {
        this.status = UserStatus.SUSPENDED;
        this.suspendedUntil = LocalDateTime.now(); // TODO 구체적 정지기간 정해야함
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = UserStatus.ACTIVE;
        if (role == null) role = Role.USER;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

package com.liquordb.user.entity;

import com.liquordb.like.entity.Like;
import com.liquordb.review.entity.Review;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id // DB상 ID
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email; // 로그인은 DB상 ID가 아닌 이메일 또는 닉네임으로 함.

    @Column(nullable = false, unique = true, length = 30)
    private String nickname;

    @Column(nullable = false, length = 255)
    private String password; // 소셜 로그인시에도 회원정보 수정을 위해 비밀번호 설정은 필요.

    private String socialProvider; // 소셜로그인 제공자 (google, kakao 등)

    private String profileImageUrl;

    @Enumerated(EnumType.STRING) //
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserTag> userTags = new HashSet<>();

    private UserStatus status;

    // 활동 제한 해제 일시
    @Column
    private LocalDateTime restrictedUntil;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Role {
        USER, ADMIN // 유저 계정, 관리자 계정
    }

    // 유저 활동 제한 (댓글, 리뷰 작성)
    public void restrict() {
        this.status = UserStatus.BANNED;
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

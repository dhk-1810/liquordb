package com.liquordb.entity;

import com.liquordb.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
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
    private String password;

    private String socialProvider; // 소셜로그인 제공자 (google, kakao 등)

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    private int reportCount = 0; // 신고된 건수

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

    @Builder(access = AccessLevel.PRIVATE)
    private User(String email, String nickname, String password, String socialProvider, Role role, File profileImage) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.socialProvider = socialProvider;
        this.role = role;
        this.profileImage = profileImage;
    }

    public static User create(String email, String nickname, String password, String socialProvider, Role role, File profileImage){
        return User.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .socialProvider(socialProvider)
                .role(role)
                .profileImage(profileImage)
                .build();
    }

    public void update(String email, String nickname){
        if (email != null) {
            this.email = email;
        }
        if (nickname != null) {
            this.nickname = nickname;
        }
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void setProfileImage(File profileImage) {
        this.profileImage = profileImage;
    }

    public void deleteProfileImage() {
        this.profileImage = null;
    }

    public void incraseReportCount() {
        if (this.reportCount <= 0) return;
        this.reportCount++;
    }

    public void withdraw() {
        if (!this.status.isActiveUser()) return;
        this.status = UserStatus.WITHDRAWN;
        this.withdrawnAt = LocalDateTime.now();
    }

    public void restore() {
        if (this.status.isActiveUser()) return;
        if (LocalDateTime.now().isBefore(this.suspendedUntil)) {
            this.status = UserStatus.SUSPENDED;
        } else {
            this.status = UserStatus.ACTIVE;
        }
    }

    // 유저 활동 제한 (댓글, 리뷰 작성)
    public void suspend(LocalDateTime suspendedUntil) {
        this.status = UserStatus.SUSPENDED;
        this.suspendedUntil = suspendedUntil;
    }
    public void lift() {
        this.status = UserStatus.ACTIVE;
    }



}

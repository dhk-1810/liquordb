package com.liquordb.entity;

import com.liquordb.enums.Role;
import com.liquordb.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.security.core.parameters.P;

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
    private String username;

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

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Review> reviews = new ArrayList<>();
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Comment> comments = new ArrayList<>();
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<LiquorLike> liquorLikes = new HashSet<>();
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<ReviewLike> reviewLikes = new HashSet<>();
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<CommentLike> commentLikes = new HashSet<>();
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<UserTag> userTags = new HashSet<>();

    // 활동 제한 해제 일시
    @Column
    private LocalDateTime suspendedUntil;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime bannedAt;
    private LocalDateTime withdrawnAt;


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

    @Builder
    private User(String email, String username, String password, String socialProvider, Role role, File profileImage) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.socialProvider = socialProvider;
        this.role = role;
        this.profileImage = profileImage;
    }

    public static User create(String email, String username, String password, String socialProvider, Role role, File profileImage){
        return User.builder()
                .email(email)
                .username(username)
                .password(password)
                .socialProvider(socialProvider)
                .role(role)
                .profileImage(profileImage)
                .build();
    }

    public void update(String email, String username){
        if (email != null) {
            this.email = email;
        }
        if (username != null) {
            this.username = username;
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

    public void increaseReportCount() {
        if (this.reportCount <= 0) return;
        this.reportCount++;
    }

    public void ban(){
        if (!this.status.isActiveUser()) return;
        this.status = UserStatus.BANNED;
        this.bannedAt = LocalDateTime.now();
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

    // 유저 제한 해제 (신고 반려 또는 제재 기한 만료)
    public void lift() {
        this.status = UserStatus.ACTIVE;
    }


}

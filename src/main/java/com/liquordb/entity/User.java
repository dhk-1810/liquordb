package com.liquordb.entity;

import com.liquordb.enums.Role;
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
    @GeneratedValue(strategy = GenerationType.UUID)
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
    private UserStatus status;

    private String profileImageKey;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
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
    private User(String email, String username, String password, String socialProvider, Role role) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.socialProvider = socialProvider;
        this.role = role;
        this.status = UserStatus.ACTIVE;
    }

    public static User create(String email, String username, String password, String socialProvider){
        return User.builder()
                .email(email)
                .username(username)
                .password(password)
                .socialProvider(socialProvider)
                .role(Role.USER)
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

    public void updateRole(Role role){
        this.role = role;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void setProfileImage(String profileImageKey) {
        this.profileImageKey = profileImageKey;
    }

    public void deleteProfileImage() {
        this.profileImageKey = null;
    }

    public void withdraw() {
        if (!this.status.isAvailable()) return;
        this.status = UserStatus.WITHDRAWN;
        this.withdrawnAt = LocalDateTime.now();
    }

    public void restore() {
        if (this.status.isAvailable()) return;
        this.status = UserStatus.ACTIVE;
    }

}

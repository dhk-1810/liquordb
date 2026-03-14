package com.liquordb.entity;

import com.liquordb.enums.Role;
import com.liquordb.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
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

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime withdrawnAt;

    private User(String email, String username, String password, String socialProvider, UserStatus status, Role role) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.status = status;
        this.role = role;
        this.socialProvider = socialProvider;
    }

    public static User create(String email, String username, String password, String socialProvider){
        return new User(email, username, password, socialProvider, UserStatus.ACTIVE, Role.USER);
    }

    public void updateEmail(String email){
        if (email != null) {
            this.email = email;
        }
    }

    public void updateUsername(String username){
        if (username != null) {
            this.username = username;
        }
    }

    public void updateRole(Role role){
        if (role != null) {
            this.role = role;
        }
    }

    public void updatePassword(String newEncryptedPassword) {
        this.password = newEncryptedPassword;
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

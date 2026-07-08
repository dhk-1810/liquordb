package com.liquordb.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "notifications")
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "receiver_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID receiverId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private Notification(UUID receiverId, String title, String content) {
        this.receiverId = receiverId;
        this.title = title;
        this.content = content;
        this.isRead = false;
    }

    public static Notification create(UUID receiverId, String title, String content){
        return new Notification(receiverId, title, content);
    }

    public void read(){
        this.isRead = true;
    }
}

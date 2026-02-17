package com.liquordb.entity;

import com.liquordb.dto.notice.NoticeRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notices")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private boolean isPinned = false;
    private boolean isDeleted;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void update(NoticeRequest request) {
        if (request.title() != null) {
            this.title = request.title();
        }
        if (this.content != null) {
            this.content = request.content();
        }
        if (this.title != null || this.content != null){
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void togglePin(){
        this.isPinned = !this.isPinned;
    }

    public void softDelete(){
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    @Builder
    private Notice(User author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
    }

    public static Notice create(User author, String title, String content){
        return Notice.builder()
                .author(author)
                .title(title)
                .content(content)
                .build();
    }

}

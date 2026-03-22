package com.liquordb.entity;

import com.liquordb.dto.notice.NoticeRequest;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notices")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // TODO 이미지

    @Column(nullable = false)
    private boolean isPinned;

    @Column(nullable = false)
    private boolean isDeleted;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    public void update(NoticeRequest request) {
        if (request.title() != null) {
            this.title = request.title();
        }
        if (request.content() != null) {
            this.content = request.content();
        }
    }

    public void togglePin(){
        this.isPinned = !this.isPinned;
    }

    public void softDelete(){
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    private Notice(User author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.isDeleted = false;
        this.isPinned = false;
    }

    public static Notice create(User author, String title, String content){
        return new Notice(author, title, content);
    }

}

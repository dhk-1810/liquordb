package com.liquordb.entity;

import com.liquordb.entity.reviewdetail.ReviewDetail;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double rating;

    private String title;

    @Lob
    private String content;

    @Column(nullable = false)
    private ReviewStatus status = ReviewStatus.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "liquor_id")
    private Liquor liquor;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReviewLike> likes = new HashSet<>();

    @OneToOne(mappedBy = "review", cascade = CascadeType.ALL)
    private ReviewDetail detail;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> images;

    @OneToMany(mappedBy = "review")
    private List<Report> reports = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime hiddenAt;
    private LocalDateTime deletedAt;

    public enum ReviewStatus {
        ACTIVE, HIDDEN, DELETED
    }

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

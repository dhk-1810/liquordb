package com.liquordb.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 파일 메타데이터 엔터티
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "files")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filePath;
    private String fileName;
    private String fileExtension;
    private Long size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    private File(String filePath, String fileName, String fileExtension, Long size, Review review, User user) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.size = size;
        this.review = review;
        this.user = user;
    }

    public static File create(String filePath, String fileName, String fileExtension, Long size, Review review, User user){
        return File.builder()
                .filePath(filePath)
                .fileName(fileName)
                .fileExtension(fileExtension)
                .size(size)
                .review(review)
                .user(user)
                .build();
    }
}

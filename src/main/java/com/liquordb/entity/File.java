package com.liquordb.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private String key;

    private String presignedUrl;

    @Enumerated(EnumType.STRING)
    private FileType type;

    public enum FileType {
        PROFILE,
        LIQUOR,
        REVIEW
    }

    private File(String key, String presignedUrl, FileType type) {
        this.key = key;
        this.presignedUrl = presignedUrl;
        this.type = type;
    }

    public static File create(String filePath, String fileName, FileType fileType) {
        return new File(filePath, fileName, fileType);
    }
}

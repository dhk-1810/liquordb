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

    private String s3key;

    private String name;

    @Enumerated(EnumType.STRING)
    private FileType type;

    public enum FileType {
        PROFILE,
        LIQUOR,
        REVIEW
    }

    private File(String key, String name, FileType type) {
        this.s3key = key;
        this.name = name;
        this.type = type;
    }

    public static File create(String key, String name, FileType type) {
        return new File(key, name, type);
    }
}

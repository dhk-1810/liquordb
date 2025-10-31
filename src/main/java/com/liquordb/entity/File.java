package com.liquordb.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 이미지파일 엔터티입니다.
 * 프로필 사진, 리뷰 이미지로 사용합니다.
 * 메타데이터와 경로만 저장하며, 실제 파일은 로컬에 저장합니다.
 * 리뷰에 사진은 0장 이상 6장 이하 업로드 가능합니다.
 */
@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}

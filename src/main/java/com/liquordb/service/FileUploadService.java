package com.liquordb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 *
 * 실제 서비스에서 AWS S3 같은 클라우드 스토리지를 사용하려면 별도 SDK 연동 필요.
 * 업로드된 파일에 대한 접근 경로나 URL 매핑도 따로 설정해야 함.
 */

@Service
@Slf4j
public class FileUploadService {

    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 없습니다.");
        }

        try {
            // 디렉토리 없으면 생성
            // 프로젝트 루트 아래 uploads 폴더에 저장
            String uploadDir = "uploads/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 고유 파일명 생성 (UUID + 원본 확장자)
            String originalFilename = file.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String newFileName = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(newFileName);

            // 파일 저장
            file.transferTo(filePath.toFile());

            // 저장된 파일 경로 또는 URL 반환 (필요에 따라 URL로 변경 가능)
            return uploadDir + newFileName;

        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.");
        }
    }
}

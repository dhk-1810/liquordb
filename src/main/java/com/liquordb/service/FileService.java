package com.liquordb.service;

import com.liquordb.entity.File;
import com.liquordb.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 실제 서비스에서 AWS S3 같은 클라우드 스토리지를 사용하려면 별도 SDK 연동 필요.
 * 업로드된 파일에 대한 접근 경로나 URL 매핑도 따로 설정해야 함.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    @Value("${file.upload-dir}") // application.yml에서 지정
    private String uploadDir;

    private final FileRepository fileRepository;

    @Transactional
    public File upload(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 없습니다.");
        }

        try {
            // 디렉토리 없으면 생성
            // 프로젝트 루트 아래 uploads 폴더에 저장
            String uploadDir = "images/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일명 생성 (UUID + 원본 확장자)
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String newFileName = UUID.randomUUID() + extension;
            Path filePath = uploadPath.resolve(newFileName);

            // 파일 로컬 저장
            file.transferTo(filePath.toFile());

            // DB에 메타데이터 저장
            File metadata = File.builder()
                    .filePath(uploadDir + newFileName)
                    .fileName(originalFilename)
                    .fileExtension(extension.replace(".", ""))
                    .size(file.getSize())
                    .build();

            fileRepository.save(metadata);

            // 메타데이터 객체 반환
            return metadata;

        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.");
        }
    }

    @Transactional(readOnly = true)
    public File findImageById(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("존재하지 않는 파일입니다."));
    }

    @Transactional
    public void delete(Long imageId) {

        File image = fileRepository.findById(imageId)
                .orElseThrow(() -> new FileNotFoundException("파일을 찾을 수 없습니다."));


        // 로컬 파일 삭제
        Path filePath = Paths.get(uploadDir).resolve(image.getFilePath()).normalize();
        try{
            Files.delete(filePath);
        } catch (IOException e){
            throw new RuntimeException("파일 삭제 실패: " + e.getMessage(), e);
        }

        // 메타데이터 삭제
        fileRepository.delete(image);
    }

}

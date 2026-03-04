package com.liquordb.service;

import com.liquordb.dto.FileResponseDto;
import com.liquordb.entity.File;
import com.liquordb.exception.file.FileNotFoundException;
import com.liquordb.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class FileService {

    private final S3Service s3Service; // 단방향 참조
    private final FileRepository fileRepository;

    @Transactional
    public FileResponseDto upload(MultipartFile file, File.FileType type) {

        if (file == null || file.isEmpty()) {
            throw new FileNotFoundException();
        }

        if (file.getSize() <= 0 || file.getSize() > 1024 * 30) {
            throw new RuntimeException("File size must be between 1 and 30MB");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = extractExtension(originalFilename);

        String key = generateS3Key(type, extension);

        try {
            s3Service.uploadFile(key, file);
        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.");
        }

        File metadata = File.create(key, file.getName(), type);
        fileRepository.save(metadata);

        return FileResponseDto.toDto(metadata);
    }

    // Key 예시: reviews/2026/03/04/abc-123-def.jpg
    private String generateS3Key(File.FileType type, String extension) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return String.format("%s/%s/%s%s",
                type.name().toLowerCase(),
                datePath,
                UUID.randomUUID(),
                extension
        );
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }

    // TODO 삭제?
}

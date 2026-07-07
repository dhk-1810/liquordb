package com.liquordb.service;

import com.liquordb.dto.FileResponseDto;
import com.liquordb.entity.File;
import com.liquordb.exception.file.FileNotFoundException;
import com.liquordb.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class FileService {

    private final S3Service s3Service; // 단방향 참조
    private final FileRepository fileRepository;

    private static final long MAX_FILE_SIZE = 30 * 1024 * 1024;

    @Transactional
    public FileResponseDto uploadAndSave(MultipartFile file, File.FileType type, Object id) {

        if (file == null || file.isEmpty()) {
            throw new FileNotFoundException();
        }

        if (file.getSize() <= 0 || file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size must be between 1 and 30MB");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = extractExtension(originalFilename);

        String key = generateS3Key(type, extension, id);

        try {
            s3Service.upload(key, file);
        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.");
        }

        File metadata = File.create(key, file.getName(), type);
        fileRepository.save(metadata);

        return FileResponseDto.toDto(metadata);
    }

    @Transactional
    public void delete(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        s3Service.deleteFile(key);
        fileRepository.findByS3key(key)
                .ifPresent(fileRepository::delete);
    }
    // Key 예시: reviews/id/abc-123-def.jpg

    private String generateS3Key(File.FileType type, String extension, Object id) {
        String idPath = String.valueOf(id);
        return String.format("%s/%s/%s%s",
                type.name().toLowerCase(),
                idPath,
                UUID.randomUUID(),
                extension
        );
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }

}

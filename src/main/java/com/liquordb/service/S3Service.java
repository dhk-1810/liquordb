package com.liquordb.service;

import com.liquordb.config.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Service
public class S3Service {

    private final S3Properties properties;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public void uploadFile(String key, MultipartFile file) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(properties.bucketName())
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    }

    public String createPresignedUrl(String key, Duration timeout) {
        GetObjectPresignRequest presignedRequest = GetObjectPresignRequest.builder()
                .signatureDuration(timeout)
                .getObjectRequest(builder -> builder.bucket(properties.bucketName()).key(key))
                .build();
        return s3Presigner.presignGetObject(presignedRequest).url().toString();
    }


}

package com.liquordb.service;

import com.liquordb.config.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class S3Service {

    private final S3Properties s3Properties;
    private final S3Client s3Client;

    private static final String DEFAULT_LIQUOR_IMAGE_SUFFIX = "default-liquor.png";
    private static final String DEFAULT_PROFILE_IMAGE_SUFFIX = "default-profile.png";

    public void upload(String key, MultipartFile file) throws IOException {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.bucketName())
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    }

    public void deleteFile(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(s3Properties.bucketName())
                .key(key)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }

    public String getLiquorImageUrl(String key) {
        if (key == null || key.isBlank()) {
            return "/" + DEFAULT_LIQUOR_IMAGE_SUFFIX;
        }
        return s3Properties.cloudfrontDomain() + key;
    }

    public String getReviewImageUrl(String key) {
        return s3Properties.cloudfrontDomain() + key;
    }

    public String getProfileImageUrl(String key) {
        if (key == null || key.isBlank()) {
            return "/" + DEFAULT_PROFILE_IMAGE_SUFFIX;
        }
        return s3Properties.cloudfrontDomain() + key;
    }

}

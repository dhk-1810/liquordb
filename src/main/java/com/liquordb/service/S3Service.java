package com.liquordb.service;

import com.liquordb.config.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class S3Service {

    private final S3Properties s3Properties;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public void uploadFile(String key, MultipartFile file) throws IOException {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.bucketName())
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    }

    public String getLiquorImageUrl(String key) {
        if (key == null || !key.isBlank()) {
            return s3Properties.cloudfrontDomain() + "default-liquor-image.png";
        }
        return s3Properties.cloudfrontDomain() + key;
    }

    public String getReviewImageUrl(String key) {
        return s3Properties.cloudfrontDomain() + key;
    }

    public String getProfileImageUrl(String key) {
        if (key == null || key.isBlank()) {
            return s3Properties.cloudfrontDomain() + "default-profile-image.png";
        }
        return s3Properties.cloudfrontDomain() + key;
    }

//    public String createPresignedUrl(String key) {
//
//        GetObjectPresignRequest presignedRequest = GetObjectPresignRequest.builder()
//                .signatureDuration(Duration.ofMinutes(s3Properties.presignedUrlExpiration()))
//                .getObjectRequest(builder -> builder.bucket(s3Properties.bucketName()).key(key))
//                .build();
//        return s3Presigner.presignGetObject(presignedRequest).url().toString();
//    }

}

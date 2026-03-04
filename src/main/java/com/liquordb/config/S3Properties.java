package com.liquordb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "liquordb.storage.s3")
public record S3Properties (
        String region,
        String accessKey,
        String secretKey,
        String bucketName,
        long presignedUrlExpiration
) {

}

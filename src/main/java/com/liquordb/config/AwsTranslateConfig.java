package com.liquordb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;

@Configuration
public class AwsTranslateConfig {

    @Bean
    public TranslateClient translateClient(S3Properties properties) {
        final Region region = Region.of(properties.region());
        final String accessKey = properties.accessKey();
        final String secretKey = properties.secretKey();

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return TranslateClient.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}

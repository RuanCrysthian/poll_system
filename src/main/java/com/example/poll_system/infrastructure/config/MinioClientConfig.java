package com.example.poll_system.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;

@Configuration
public class MinioClientConfig {

    @Value("${app.minio.url}")
    private String minioEndpoint;

    @Value("${app.minio.access-key}")
    private String minioAcessKey;

    @Value("${app.minio.secret-key}")
    private String minioSecretKey;

    @Value("${app.minio.bucket-name}")
    private String minioBucketName;

    @Bean
    MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioEndpoint)
                .credentials(minioAcessKey, minioSecretKey)
                .build();
    }

    @Bean
    String minioBucket(MinioClient minioClient) {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(minioBucketName)
                            .build());
            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(minioBucketName)
                                .build());
            }

            return minioBucketName;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao configurar bucket MinIO", e);
        }
    }
}

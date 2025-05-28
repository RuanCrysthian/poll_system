package com.example.poll_system.infrastructure.services.impl;

import java.io.InputStream;
import java.time.LocalDateTime;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import com.example.poll_system.infrastructure.services.ObjectStorage;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

@Service
public class MinioStorage implements ObjectStorage {

    private final MinioClient minioClient;

    public MinioStorage(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public String upload(String fileName, InputStream fileContent) throws Exception {
        String name = fileName + "-" + LocalDateTime.now().toString();
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket("images")
                        .object(name)
                        .stream(fileContent, fileContent.available(), -1)
                        .contentType("image/jpeg")
                        .build());
        return name;
    }

    @Override
    public byte[] download(String fileName) throws Exception {
        var stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket("images")
                        .object(fileName)
                        .build());
        return IOUtils.toByteArray(stream);
    }

    @Override
    public void delete(String fileName) {
        // Implement the delete logic using MinIO client
    }

}

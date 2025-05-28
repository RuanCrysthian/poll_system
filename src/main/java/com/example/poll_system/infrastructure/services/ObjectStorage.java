package com.example.poll_system.infrastructure.services;

import java.io.InputStream;

public interface ObjectStorage {
    String upload(String fileName, InputStream fileContent) throws Exception;

    byte[] download(String fileName) throws Exception;

    void delete(String fileName);
}

package com.alipay.autotuneservice.minio;

import io.minio.MinioClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IMinioClientTest {

    @Autowired
    private IMinioClient minioClient;

    @Test
    void getMinioClient() {
        MinioClient minioClient = this.minioClient.getMinioClient();
        assertTrue(minioClient!=null);
    }

    @Test
    void checkBucketExists() {
    }

    @Test
    void checkObjectExists() {
    }

    @Test
    void uploadObject() {
        String filePath = "/tmp/minio/hello.txt";
        String bucket = "tmaestro";
        String fileName = "hello.txt";

        MinioUploadFileResult result = minioClient.uploadObject(bucket, filePath, fileName);

        assertTrue(result.getSuccess());
    }

    @Test
    void downloadObject() {
        String downloadFilePath = "/tmp/minio/download/hello.txt";
        String bucket = "tmaestro";
        String fileName = "hello.txt";

        MinioDownloadObjectResult result = minioClient.downloadObject(bucket, fileName, downloadFilePath);

        assertTrue(result.isObjectExists());

    }
}
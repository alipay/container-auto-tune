/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.minio;

import com.alibaba.fastjson.JSON;
import io.minio.BucketExistsArgs;
import io.minio.DownloadObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author huangkaifei
 * @version : MinioClientImpl.java, v 0.1 2022年11月09日 2:25 AM huangkaifei Exp $
 */
@Slf4j
@Component
public class MinioClientImpl implements IMinioClient {

    private static final String MINIO_API_SERVER = "http://localhost:30087";

    private static final String MINIO_ACCESS_KEY = "minioadmin";
    private static final String MINIO_SECRET_KEY = "minioadmin";

    private MinioClient minioClient = null;

    @PostConstruct
    public void initMinioClient() {
        this.minioClient = getClient();
    }

    private MinioClient getClient() {
        if (minioClient == null) {
            return MinioClient.builder()
                    .endpoint(MINIO_API_SERVER)
                    .credentials(MINIO_ACCESS_KEY, MINIO_SECRET_KEY)
                    .build();
        }
        return minioClient;
    }

    @Override
    public MinioClient getMinioClient() {
        return getClient();
    }

    @Override
    public Boolean checkBucketExists(String bucket) {
        try {
            return getMinioClient().bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        } catch (Exception e) {
            throw new RuntimeException("");
        }
    }

    @Override
    public Boolean checkObjectExists(String bucket, String object) {
        try {
            // TO BE SUPPORTED
            return false;
        } catch (Exception e) {
            throw new RuntimeException("");
        }
    }

    @Override
    public MinioUploadFileResult uploadObject(String bucket, String filePath, String fileName) {
        try {
            boolean found = getMinioClient().bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!checkBucketExists(bucket)) {
                // Make a new bucket called 'asiatrip'.
                log.info("bucket={} not exists, so will create it.", bucket);
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
            ObjectWriteResponse response = minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .filename(filePath)
                            .build());
            if (response == null) {
                throw new UnsupportedOperationException("uploadObject failed.");
            }
            MinioUploadFileResult uploadFIleResult = MinioUploadFileResult.builder()
                    .eTag(response.etag())
                    .bucket(response.bucket())
                    .object(response.object())
                    .build();

            log.info("uploadObject res={}", JSON.toJSONString(uploadFIleResult));
            return uploadFIleResult;
        } catch (Exception e) {
            log.error("uploadObject occurs an error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public MinioDownloadObjectResult downloadObject(String bucket, String object, String downloadFilePath) {
        try {
            DownloadObjectArgs build = DownloadObjectArgs.builder()
                    .bucket(bucket)
                    .filename(downloadFilePath)
                    .object(object)
                    .overwrite(true)
                    .build();
            getMinioClient().downloadObject(build);
            log.info("download object={} successful.", object);
            return MinioDownloadObjectResult.builder()
                    .bucket(bucket)
                    .object(object)
                    .isObjectExists(true)
                    .build();
        } catch (Exception e) {
            log.error("downloadObject occurs an error.", e);
            throw new RuntimeException(e);
        }
    }

}
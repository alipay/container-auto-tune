/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.base.minio;

import io.minio.MinioClient;

/**
 * @author huangkaifei
 * @version : IMinioClient.java, v 0.1 2022年11月09日 2:19 AM huangkaifei Exp $
 */
public interface IMinioClient {

    /**
     * Get Minio client
     *
     * @return MinioClient object
     */
    MinioClient getMinioClient();

    /**
     * Check whether the bucket exists
     *
     * @param bucket bucket name
     * @return true - exist, false - not exist
     */
    Boolean checkBucketExists(String bucket);

    /**
     * Check whether the object exists in bucket
     *
     * @param bucket bucket name
     * @param object object name
     * @return true - exist, false - not exist
     */
    Boolean checkObjectExists(String bucket, String object);

    /**
     * Upload file to minio server
     *
     * @param bucket bucket name
     * @param filePath file path
     * @param fileName file name
     * @return MinioUploadFileResult object
     */
    MinioUploadFileResult uploadObject(String bucket, String filePath, String fileName);

    /**
     * Download object from minio server
     * note: minio server will put the download file to downloadFilePath path.
     *
     * @param bucket bucket name
     * @param object the object name stored in Minio server
     * @param downloadFilePath the file path to put the download fiel
     * @return
     */
    MinioDownloadObjectResult downloadObject(String bucket, String object, String downloadFilePath);
}
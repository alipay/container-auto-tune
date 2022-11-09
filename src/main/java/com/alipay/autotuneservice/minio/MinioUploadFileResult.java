/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.minio;

import lombok.Builder;
import lombok.Data;

/**
 * @author huangkaifei
 * @version : MinioUploadFileResult.java, v 0.1 2022年11月09日 1:33 AM huangkaifei Exp $
 */
@Data
@Builder
public class MinioUploadFileResult {
    /**
     * The unique key to the upload file
     */
    private String eTag;
    /**
     * The bucket name
     */
    private String bucket;
    /**
     * The object name in minio
     */
    private String object;
    /**
     * The result of uploading file
     */
    private Boolean success;
}
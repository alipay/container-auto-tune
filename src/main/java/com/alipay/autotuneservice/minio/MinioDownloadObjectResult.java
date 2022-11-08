/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.minio;

import lombok.Builder;
import lombok.Data;

/**
 * @author huangkaifei
 * @version : MinioDownloadObjectResult.java, v 0.1 2022年11月09日 2:44 AM huangkaifei Exp $
 */
@Data
@Builder
public class MinioDownloadObjectResult {

    private String bucket;
    private String object;
    private boolean isObjectExists;
    private String message;
}
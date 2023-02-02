/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alipay.autotuneservice.util;

import com.alipay.autotuneservice.base.h2.H2DBManagerImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;

/**
 * @author huangkaifei
 * @version : FileUtil.java, v 0.1 2023年02月01日 4:38 PM huangkaifei Exp $
 */
@Slf4j
public class FileUtil {

    /**
     * copy file from resource path to target path
     *
     * @param resourceFilePath resource path
     * @param targetFilePath target path
     */
    public static void copyfileFromResourcePath(String resourceFilePath, String targetFilePath){
        try {
            log.info("*****  start to copyfileFromResourcePath.  *****");
            InputStream resourceAsStream = H2DBManagerImpl.class.getClassLoader().getResourceAsStream(resourceFilePath);
            File file = new File(targetFilePath);
            org.apache.commons.io.FileUtils.copyInputStreamToFile(resourceAsStream, file);
            log.info("*****  end to copyMinioFile.  *****");
        }catch (Exception e){
            log.error("copyfileFromResourcePath occurs an error.", e);
        }
    }
}
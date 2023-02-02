/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.base.minio;

import com.alipay.autotuneservice.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Embed the minio server
 *
 * @author huangkaifei
 * @version : MinioServerStarter.java, v 0.1 2022年11月09日 1:29 PM huangkaifei Exp $
 */
@Slf4j
@Component
public class MinioServerStarter {

    private static String START_MINIO_SCRIPT = "/tmp/minio-server/start-minio.sh";
    private static String START_MINIO_CMD    = "/bin/sh /tmp/minio-server/start-minio.sh";

    @PostConstruct
    public void startMinioServer() {
        try {
            start();
        } catch (Exception e) {
            log.error("init minio-server occurs an error.", e);
        }
    }

    private void start() {
        copyMinioFiles();
        execCmd();
    }

    private static void execCmd() {
        try {
            Runtime.getRuntime().exec(START_MINIO_CMD);
        } catch (Exception e) {
            log.error("occurs an error.", e);
        }
    }

    private static void copyMinioFiles() {
        try {
            log.info("*****  start to copyMinioFile.  *****");
            FileUtil.copyfileFromResourcePath("minio/minio", "/tmp/minio-server/minio");
            log.info("*****  end to copyMinioFile.  *****");
            FileUtil.copyfileFromResourcePath("minio/start-minio.sh", START_MINIO_SCRIPT);
        } catch (Exception e) {
            log.error("copyMinioFile occurs an error.");
        }
    }
}
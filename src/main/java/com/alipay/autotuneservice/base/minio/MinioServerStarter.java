/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.base.minio;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Embed the minio server
 *
 * @author huangkaifei
 * @version : MinioServerStarter.java, v 0.1 2022年11月09日 1:29 PM huangkaifei Exp $
 */
@Slf4j
@Component
public class MinioServerStarter {

    @PostConstruct
    public void startMinioServer() {
        start();
    }

    private boolean execCmd(String cmd) {
        try {
            Process exec = Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", cmd});
            if (exec.waitFor() != 0) {
                log.error("execCmd={} failed. errorMsg={}", cmd, convert(exec.getErrorStream()));
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("execCmd={} occurs an error.", cmd, e);
            return false;
        }
    }

    private String convert(InputStream inputStream) throws IOException {
        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
    }

    private void start() {
        //if (!execCmd("ls /tmp/minio-server")) {
        //    execCmd("mkdir /tmp/minio-server");
        //}
        //log.info("*********start download minio********");
        //String downloadCmd = "curl -o /tmp/minio-server/minio https://dl.min.io/server/minio/release/linux-amd64/minio";
        //if (!execCmd(downloadCmd)) {
        //    log.error("download minio file failed.");
        //    return;
        //}
        //log.info("start chmod /tmp/minio-server/minio......");
        //String chmodCmd = "chmod +x  /tmp/minio-server/minio";
        //if (!execCmd(chmodCmd)) {
        //    log.error("chmod /tmp/minio-server/minio failed");
        //    return;
        //}
        //log.info("start minio server.....");
        //String startMinioCmd = "/tmp/minio-server/minio server /tmp/minio-db --address :9098 --console-address :9099";
        //if (!execCmd(startMinioCmd)) {
        //    log.error("start minio server failed");
        //    return;
        //}
    }
}
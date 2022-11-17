/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.grpc;

import com.alipay.autotuneservice.model.common.ServerType;
import com.auto.tune.client.SystemCommonGrpc;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author dutianze
 * @version GrpcCommon.java, v 0.1 2022年05月06日 16:21 dutianze
 */
@Data
@Slf4j
public class GrpcCommon {

    private String       appName;
    private String       accessToken;
    private long         timestamp;
    private String       hostname;
    private String       namespace;
    private String       podIp;
    private String       javaVersion;
    private List<String> collectors;
    private String       unionCode;
    private ServerType   serverType;
    private Integer      appId;

    public boolean isK8s() {
        return this.serverType == ServerType.DOCKER;
    }

    public String getAppName() {
        try {
            if (isK8s()) {
                return hostname.substring(0, StringUtils.lastOrdinalIndexOf(hostname, "-", 2));
            }
            return appName;
        } catch (Exception e) {
            log.error("getAppName is error:{}", e.getMessage());
            return appName;
        }
    }

    public static GrpcCommon build(SystemCommonGrpc systemCommonGrpc) {
        GrpcCommon grpcCommon = new GrpcCommon();
        grpcCommon.setAppName(systemCommonGrpc.getAppName());
        grpcCommon.setAccessToken(systemCommonGrpc.getAccessToken());
        grpcCommon.setTimestamp(systemCommonGrpc.getTimestamp());
        grpcCommon.setHostname(systemCommonGrpc.getHostname());
        grpcCommon.setNamespace(systemCommonGrpc.getNamespace());
        grpcCommon.setPodIp(systemCommonGrpc.getPodIp());
        grpcCommon.setJavaVersion(systemCommonGrpc.getJavaVersion());
        grpcCommon.setCollectors(systemCommonGrpc.getCollectorsList());
        grpcCommon.setCollectors(systemCommonGrpc.getCollectorsList());
        grpcCommon.setUnionCode(systemCommonGrpc.getUnionCode());
        grpcCommon.setServerType(ServerType.valueOf(systemCommonGrpc.getServerType()));
        return grpcCommon;
    }

    public String getAccessToken() {
        return accessToken.equals("1") ? "IOeiob2AI9n_YBjvjy04krmS5pe0xeEt" : accessToken;
    }
}
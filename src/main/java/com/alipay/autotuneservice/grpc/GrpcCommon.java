/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.autotuneservice.grpc;

import com.alipay.autotuneservice.model.common.ServerType;
import com.auto.tune.client.SystemCommonGrpc;
import com.google.common.base.Preconditions;
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
    private long         jvmJitTime;
    private String       jvmConfig;

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
        Preconditions.checkArgument(systemCommonGrpc != null, "systemCommonGrpc is null.");
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
        grpcCommon.setServerType(ServerType.valueOfName(systemCommonGrpc.getServerType()));
        grpcCommon.setJvmJitTime(systemCommonGrpc.getJvmJitTime());
        grpcCommon.setJvmConfig(systemCommonGrpc.getJvmConfig());
        return grpcCommon;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
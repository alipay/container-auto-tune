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

import com.auto.tune.client.SystemCommonGrpc;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author dutianze
 * @version GrpcCommon.java, v 0.1 2022年05月06日 16:21 dutianze
 */
@Data
public class GrpcCommon {

    private String       appName;
    private String       accessToken;
    private long         timestamp;
    private String       hostname;
    private String       namespace;
    private String       podIp;
    private String       javaVersion;
    private List<String> collectors;

    public boolean isK8s() {
        return StringUtils.isNotBlank(namespace);
    }

    public String getAppName() {
        if (isK8s()) {
            return hostname.substring(0, StringUtils.lastOrdinalIndexOf(hostname, "-", 2));
        }
        return appName;
    }

    public String generateFileName(String fileName) {
        return accessToken + "_" + getAppName() + "_" + fileName;
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
        return grpcCommon;
    }

    public String getAccessToken() {
        return accessToken.equals("1") ? "xx" : accessToken;
    }
}
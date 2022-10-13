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
package com.alipay.autotuneservice.controller.model.baseLine;

import lombok.Data;

import java.util.List;

/**
 * @author huoyuqi
 * @version PodLineVO.java, v 0.1 2022年08月09日 9:06 下午 huoyuqi
 */
@Data
public class PodLineVO {

    /**
     * appId
     */
    private Integer      appId;

    /**
     * podId
     */
    private Integer      podId;

    /**
     * pod名称
     */
    private String       podName;

    /**
     * ip
     */
    private String       ip;

    /**
     * java 版本
     */
    private String       javaVersion;

    /**
     * 规约 多少核内存
     */
    private String       spec;

    /**
     * pod jvm参数
     */
    private String       jvmConfig;

    /**
     * jvmMarketId 主要作用于
     */
    private Integer      currentJvmMarketId;

    /**
     * pod jvm参数详情
     */
    private List<String> podJvmDetail;

    /**
     * pod 所属jvm版本
     */
    private String       version;

    /**
     * pod compare是否可点击
     */
    private Boolean      compareOn;

    public PodLineVO(Integer appId, Integer podId, String podName, String ip, String javaVersion,
                     String spec, List<String> podJvmDetail, String version, Integer jvmMarketId,
                     Boolean compareOn) {
        this.appId = appId;
        this.podId = podId;
        this.podName = podName;
        this.ip = ip;
        this.javaVersion = javaVersion;
        this.spec = spec;
        this.podJvmDetail = podJvmDetail;
        this.version = version;
        this.currentJvmMarketId = jvmMarketId;
        this.compareOn = compareOn;
    }
}
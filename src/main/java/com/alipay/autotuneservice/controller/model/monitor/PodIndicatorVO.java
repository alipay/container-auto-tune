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
package com.alipay.autotuneservice.controller.model.monitor;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author huoyuqi
 * @version PodIndicator.java, v 0.1 2022年10月24日 10:45 上午 huoyuqi
 */
@Data
public class PodIndicatorVO {

    /**
     * container name
     */
    private String containerName;

    /**
     * nameSpace
     */
    private String nameSpace;

    /**
     * container Id
     */
    private String containerId;


    /**
     * agent name
     */
    private String agentName;

    /**
     * pod name
     */
    private String podName;

    /**
     * node name
     */
    private String nodeName;

    /**
     * node ip
     */
    private String nodeIp;

    /**
     * imageId
     */
    private String imageId;

    /**
     * jvmJitTime
     */
    private Long jvmJitTime;

    /**
     * container start time
     */
    private Long containerStarted;

    /**
     * label
     */
    private JSONObject labels;

    /**
     * pod belong to cluster
     */
    private String clusterName;

    /**
     * judge rt、qps exist
     */
    private Boolean monitorRQ;

    /**
     * 元素集合
     */
    private MetricVOS metricVOS;

    /**
     * unicode
     */
    private String unicode;

    /**
     * appName
     */
    private String appName;

    /**
     * javaVersion
     */
    private String version;

    /**
     * jvm_home
     */
    private String jvmHome;

    /**
     * classPath
     */
    private String classPath;

    /**
     * libraryPath
     */
    private String libraryPath;

    /**
     * osVersion
     */
    private String osVersion;

    /**
     * osArch
     */
    private String osArch;

    /**
     * jvm参数
     */
    private String jvmParam;

    /**
     * pid
     */
    private Integer pid;

}

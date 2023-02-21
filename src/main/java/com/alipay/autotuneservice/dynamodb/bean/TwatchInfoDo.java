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
package com.alipay.autotuneservice.dynamodb.bean;

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.controller.model.monitor.PodIndicatorVO;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author huangkaifei
 * @version : TwatchDO.java, v 0.1 2022年04月10日 10:31 PM huangkaifei Exp $
 */
@Data
@Builder
public class TwatchInfoDo implements Serializable {

    private String containerId;
    private String nameSpace;
    private String containerName;
    private String agentName;
    private Long   gmtModified;
    private String podName;
    private Long   dtPeriod;
    private String nodeName;
    private String nodeIp;
    /**
     * image
     */
    private String imageId;
    private String labels;
    /**
     * io.kubernetes.docker.type
     */
    private String type;
    /**
     * 容器启动时间
     * Created
     */
    private long   containerStarted;
    /**
     * command to start application on container .
     */
    private String command;

    public String getAgentName() {
        return agentName;
    }

    public String getPodName() {
        return podName;
    }

    public String getContainerId() {
        return containerId;
    }

    public String getNameSpace() {
        return StringUtils.isEmpty(nameSpace) ? "" : nameSpace;
    }

    public PodIndicatorVO convertVO() {
        PodIndicatorVO podIndicatorVO = new PodIndicatorVO();
        podIndicatorVO.setContainerId(this.containerId);
        podIndicatorVO.setAgentName(this.agentName);
        //秒级别转换成毫秒级别
        podIndicatorVO.setContainerStarted(this.containerStarted * 1000L);
        podIndicatorVO.setImageId(this.imageId);
        podIndicatorVO.setLabels(JSON.parseObject(labels));
        podIndicatorVO.setNameSpace(this.nameSpace);
        podIndicatorVO.setNodeIp(this.nodeIp);
        podIndicatorVO.setNodeName(this.nodeName);
        podIndicatorVO.setPodName(this.podName);
        podIndicatorVO.setContainerName(this.containerName);
        return podIndicatorVO;
    }
}
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
package com.alipay.autotuneservice.controller.model;

import com.alipay.autotuneservice.dao.jooq.tables.records.PodInfoRecord;
import com.alipay.autotuneservice.model.common.PodAttach;
import com.alipay.autotuneservice.model.common.PodAttachStatus;
import com.alipay.autotuneservice.util.DateUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author huoyuqi
 * @version PodVO.java, v 0.1 2022年06月06日 6:13 下午 huoyuqi
 */
@NoArgsConstructor
@Data
public class PodVO {

    /**
     * pod名称
     */
    private String          podName;

    /**
     * podId
     */
    private Integer         podId;

    /**
     * 是否安装agent
     */
    private PodAttachStatus status;

    /**
     * NodeName
     */
    private String          nodeName;

    /**
     * Cluster
     */
    private String          cluster;

    /**
     * 时间
     */
    private Long            time;

    public boolean hasAgent() {
        return PodAttachStatus.INSTALLED.equals(this.status);
    }

    public PodVO(PodInfoRecord podInfoRecord, String nodeName, PodAttach podAttach) {
        this.podId = podInfoRecord.getId();
        this.podName = podInfoRecord.getPodName();
        this.status = podInfoRecord.getAgentInstall() == 1 ? PodAttachStatus.INSTALLED
            : podAttach == null ? PodAttachStatus.NOT_INSTALLED : podAttach.getStatus();
        this.nodeName = nodeName;
        this.cluster = podInfoRecord.getClusterName();
        this.time = DateUtils.asTimestamp(podInfoRecord.getCreatedTime());
    }

}
/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dynamodb.bean;

import com.google.common.base.Preconditions;
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

    public String getAgentName() {
        return agentName;
    }


    public String getPodName() {
        return podName;
    }

    public String getContainerId() {
        return containerId;
    }

    public void checkInsertArgs() {
        Preconditions.checkArgument(StringUtils.isNotBlank(agentName), "agentName can not be empty.");
        Preconditions.checkArgument(StringUtils.isNotBlank(containerId), "containerId can not be empty.");
        Preconditions.checkArgument(StringUtils.isNotBlank(podName), "podName can not be empty.");
        Preconditions.checkArgument(dtPeriod != null, "dtPeriod can not be empty.");
        Preconditions.checkArgument(gmtModified != null, "gmtModified can not be empty.");
    }

    public String getNameSpace() {
        return StringUtils.isEmpty(nameSpace) ? "" : nameSpace;
    }
}
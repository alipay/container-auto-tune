/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dao;

import com.alipay.autotuneservice.dynamodb.bean.ContainerProcessInfo;

import java.util.List;

/**
 * @author huangkaifei
 * @version : ContainerProcessInfoRepository.java, v 0.1 2022年10月31日 9:42 PM huangkaifei Exp $
 */
public interface ContainerProcessInfoRepository {

    /**
     * insert ContainerProcessInfo record
     *
     * @param item  ContainerProcessInfo record
     */
    void insert(ContainerProcessInfo item);

    /**
     * query ContainerProcessInfo by container id
     *
     * @param containerId container id
     * @return
     */
    List<ContainerProcessInfo> queryProcessInfos(String containerId);
}
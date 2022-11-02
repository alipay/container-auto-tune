/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.dao;

import com.alipay.autotuneservice.dynamodb.bean.ContainerStatistics;

import java.util.List;

/**
 * @author huangkaifei
 * @version : ContainerStatisticRepository.java, v 0.1 2022年10月31日 9:08 PM huangkaifei Exp $
 */
public interface ContainerStatisticRepository {

    /**
     * insert ContainerStatistics record
     *
     * @param statistics ContainerStatistics record
     */
    void insert(ContainerStatistics statistics);

    /**
     * query by containerId and range of timestamp
     *
     * @param containerId
     * @param start
     * @param end
     * @return
     */
    List<ContainerStatistics> queryRange(String containerId, long start, long end);
}
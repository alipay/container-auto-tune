/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.service;

import com.alipay.autotuneservice.controller.model.HealthCheckVO;

/**
 * @author chenqu
 * @version : AppHealthCheckService.java, v 0.1 2022年04月26日 10:50 chenqu Exp $
 */
public interface AppHealthCheckService {

    Integer submitHealthCheck(Integer appId);

    HealthCheckVO refreshCheck(Integer healthCheckId, int count);

    HealthCheckVO healthDetail(Integer healthCheckId);

    HealthCheckVO getLastData(Integer appId);

    Integer getHealthScore(Integer appId);

}
/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.controller.model.meter;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 测试监控链接返回结果
 *
 * @author huangkaifei
 * @version : ValidateMeterResult.java, v 0.1 2022年08月23日 11:41 AM huangkaifei Exp $
 */
@Data
@Builder
public class ValidateMeterResult {

    private boolean                 success = false;
    private String                  message;
    /**
     * metric result
     */
    private List<MeterMetricResult> result;

    public static ValidateMeterResult failedResult(String errorMsg){
        return ValidateMeterResult.builder().success(false).message(errorMsg).build();
    }
}
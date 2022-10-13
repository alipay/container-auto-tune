/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.meter.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author huangkaifei
 * @version : ConfigValidateResult.java, v 0.1 2022年08月19日 4:12 PM huangkaifei Exp $
 */
@Data
@Builder
public class ConfigValidateResult {
    private boolean result = false;
    private String message;

    public static ConfigValidateResult failedResult(String errorMsg){
        return ConfigValidateResult.builder().result(false).message(errorMsg).build();
    }
}
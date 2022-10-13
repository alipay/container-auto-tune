/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.controller.model.meter;

import lombok.Data;

/**
 * @author huangkaifei
 * @version : MeterRegisterRequest.java, v 0.1 2022年08月22日 9:25 PM huangkaifei Exp $
 */
@Data
public class MeterRegisterRequest {
    private Integer appId;
    private String meterName;
    private String sererHost;
    private String rtPromQL;
    private String qpsPromQL;
}
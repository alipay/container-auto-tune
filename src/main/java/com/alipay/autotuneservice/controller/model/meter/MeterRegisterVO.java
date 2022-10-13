/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.controller.model.meter;

import lombok.Builder;
import lombok.Data;

/**
 * @author huangkaifei
 * @version : MeterRegisterVO.java, v 0.1 2022年08月22日 9:24 PM huangkaifei Exp $
 */
@Data
@Builder
public class MeterRegisterVO {
    private MeterMeta meterModel;
}
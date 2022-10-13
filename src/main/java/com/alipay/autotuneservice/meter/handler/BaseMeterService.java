/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.meter.handler;

import com.alipay.autotuneservice.controller.model.meter.MeterMeta;
import com.alipay.autotuneservice.dao.MeterMetaInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author huangkaifei
 * @version : BaseMeterService.java, v 0.1 2022年08月24日 1:58 PM huangkaifei Exp $
 */
public abstract class BaseMeterService {

    @Autowired
    private MeterMetaInfoRepository metaInfoRepository;

    public boolean saveOrUpdate(MeterMeta meterMeta){
        return metaInfoRepository.saveOrUpdate(meterMeta);
    }
}
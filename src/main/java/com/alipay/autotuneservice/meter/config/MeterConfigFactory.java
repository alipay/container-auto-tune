/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.meter.config;

import com.alipay.autotuneservice.controller.model.meter.MeterMeta;
import com.alipay.autotuneservice.dao.MeterMetaInfoRepository;
import com.alipay.autotuneservice.util.ObjectUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author huangkaifei
 * @version : MeterConfig.java, v 0.1 2022年08月26日 7:29 AM huangkaifei Exp $
 */
@Service
public class MeterConfigFactory {

    /**
     * meterMeta collection
     */
    private static final Set<MeterMeta> METER_MATAS = Sets.newHashSet();

    @Autowired
    private MeterMetaInfoRepository metaInfoRepository;

    @PostConstruct
    public void init() {
        List<MeterMeta> meterMetas = metaInfoRepository.listAppMeters();
        Optional.ofNullable(meterMetas)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(item -> item != null && StringUtils.isNotEmpty(item.getMeterName()) && ObjectUtil.checkInteger(item.getAppId()))
                .forEach(METER_MATAS::add);
    }

    public final Set<MeterMeta> getMeterMatas(){
        return METER_MATAS;
    }
}
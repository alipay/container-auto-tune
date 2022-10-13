/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.controller.model.meter;

import com.alipay.autotuneservice.meter.model.MeterMetric;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author huangkaifei
 * @version : MeterMeta.java, v 0.1 2022年08月23日 2:42 PM huangkaifei Exp $
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeterMeta implements Serializable {

    /**
     * app Id
     */
    private Integer           appId;
    /**
     * meter name
     */
    private String            meterName;
    /**
     * server domain address
     */
    private String            meterDomain;
    /**
     * whether meter enables
     */
    private boolean           meterEnable = true;
    /**
     * meter metric list
     */
    private List<MeterMetric> metricList;

    public void filterMeterMetric(){
        List<MeterMetric> collect = Optional.ofNullable(metricList).orElse(Lists.newArrayList()).stream().filter(
                item -> StringUtils.isNotBlank(item.getMetricPath())).collect(
                Collectors.toList());
        this.setMetricList(collect);
    }
}
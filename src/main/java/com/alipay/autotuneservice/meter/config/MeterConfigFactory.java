/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    //@PostConstruct
    public void init() {
        //List<MeterMeta> meterMetas = metaInfoRepository.listAppMeters();
        //Optional.ofNullable(meterMetas)
        //        .orElse(Lists.newArrayList())
        //        .stream()
        //        .filter(item -> item != null && StringUtils.isNotEmpty(item.getMeterName()) && ObjectUtil.checkInteger(item.getAppId()))
        //        .forEach(METER_MATAS::add);
    }

    public final Set<MeterMeta> getMeterMatas(){
        return METER_MATAS;
    }
}
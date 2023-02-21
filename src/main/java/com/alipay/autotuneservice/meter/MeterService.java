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
package com.alipay.autotuneservice.meter;

import com.alipay.autotuneservice.controller.model.meter.MeterMeta;
import com.alipay.autotuneservice.controller.model.meter.ValidateMeterResult;
import com.alipay.autotuneservice.dao.MeterMetaInfoRepository;
import com.alipay.autotuneservice.meter.handler.MeterHandler;
import com.alipay.autotuneservice.meter.handler.MeterHandlerFactory;
import com.alipay.autotuneservice.meter.model.MeterType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * responsibility to operate meter
 *
 * @author huangkaifei
 * @version : MeterService.java, v 0.1 2022年08月23日 9:52 PM huangkaifei Exp $
 */
@Slf4j
@Service
public class MeterService {

    private static final Map<MeterType, MeterHandler> resource = new ConcurrentHashMap<>();

    @Autowired
    private MeterMetaInfoRepository metaInfoRepository;

    @Autowired
    private MeterHandlerFactory meterHandlerFactory;

    /**
     * validate Meter
     *
     * @param meterMeta
     * @return ValidateMeterResult obj
     */
    public ValidateMeterResult validateMeter(MeterMeta meterMeta){
        meterMeta.filterMeterMetric();
        MeterType meterType = MeterType.find(meterMeta.getMeterName());
        return meterHandlerFactory.getMeterHandler(meterType).validateMeter(meterMeta);
    }

    /**
     * register meter
     *
     * @param meterMeta
     * @return
     */
    public Boolean registerMeter(MeterMeta meterMeta) {
        meterMeta.filterMeterMetric();
        MeterType meterType = MeterType.find(meterMeta.getMeterName());
        return meterHandlerFactory.getMeterHandler(meterType).register(meterMeta);
    }

    /**
     * list meters of appId
     *
     * @param appId
     * @return MeterMeta collection
     */
    public List<MeterMeta> listMeters(Integer appId){
        return metaInfoRepository.listAppMeters(appId);
    }

    /**
     * delete meter by appId and meterName
     *
     * @param appId app identity
     * @param meterName meter name
     * @return true - success;  false - failed
     */
    public Boolean deleteMeter(Integer appId, String meterName) {
        return metaInfoRepository.deleteAppMeter(appId, meterName);
    }

    /**
     * 查询meter metric
     *
     * @param meterMeta
     * @param startTime
     * @param endTime
     * @return
     */
    public Map<String, String> queryMeterMetric(MeterMeta meterMeta, long startTime, long endTime, long step){
        MeterType meterType = MeterType.find(meterMeta.getMeterName());
        return meterHandlerFactory.getMeterHandler(meterType).queryMetric(meterMeta,startTime, endTime, step);
    }
}
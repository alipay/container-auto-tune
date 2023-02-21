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
package com.alipay.autotuneservice.meter.handler;

import com.alipay.autotuneservice.controller.model.meter.MeterMeta;
import com.alipay.autotuneservice.controller.model.meter.ValidateMeterResult;
import com.alipay.autotuneservice.meter.MeterService;
import com.alipay.autotuneservice.meter.model.MeterType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author huangkaifei
 * @version : MeterHandler.java, v 0.1 2022年08月19日 4:27 PM huangkaifei Exp $
 */
public abstract class MeterHandler extends BaseMeterService {

    @Autowired
    private MeterHandlerFactory meterHandlerFactory;

    @Autowired
    private MeterService meterService;

    @PostConstruct
    public void register() {
        meterHandlerFactory.registerMeterHandler(this);
    }

    public abstract Map<String, String> queryMetric(MeterMeta meterMeta, long startTime, long endTime, long step);

    public abstract Boolean register(MeterMeta meterMeta);

    public abstract ValidateMeterResult validateMeter(MeterMeta meterMeta);

    public abstract MeterType getMeterType();

}
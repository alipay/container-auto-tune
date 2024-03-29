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

import com.alipay.autotuneservice.meter.model.MeterType;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huangkaifei
 * @version : MeterHandlerFactory.java, v 0.1 2022年08月26日 1:00 AM huangkaifei Exp $
 */
@Service
public class MeterHandlerFactory {
    private static final Map<MeterType, MeterHandler> METER_RESOURCE = new ConcurrentHashMap<>();

    public void registerMeterHandler(MeterHandler meterHandler){
        MeterType meterType = meterHandler.getMeterType();
        if (!METER_RESOURCE.containsKey(meterType)) {
            METER_RESOURCE.put(meterType, meterHandler);
            return;
        }
    }

    public MeterHandler getMeterHandler(MeterType meterType){
        if (METER_RESOURCE.containsKey(meterType)) {
            return METER_RESOURCE.get(meterType);
        }
        throw new UnsupportedOperationException(String.format("MeterHandlerFactory does not support meterType=%s", meterType.name()));
    }
}
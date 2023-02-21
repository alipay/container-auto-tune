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
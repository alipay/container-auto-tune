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
package com.alipay.autotuneservice.dynamodb.repository;

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.dao.MeterMetricInfoRepository;
import com.alipay.autotuneservice.dynamodb.bean.MeterMetricInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author huangkaifei
 * @version : MeterMetricInfoService.java, v 0.1 2022年10月08日 2:17 PM huangkaifei Exp $
 */
@Slf4j
@Service
public class MeterMetricInfoService {

    private static final String METER_METRIC_INFO_TABLE = "meter_metric_info";

    @Autowired
    private MeterMetricInfoRepository meterMetricRepository;

    public void insert(MeterMetricInfo meterMetricInfo) {
        try {
            meterMetricRepository.insert(meterMetricInfo);
        } catch (Exception e) {
            log.error("insert meterMetricInfo={} occurs an error", JSON.toJSONString(meterMetricInfo), e);
        }
    }
}
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
package com.alipay.autotuneservice.model.tune.trail;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuningParamTrialDataRecord;
import com.alipay.autotuneservice.model.common.TrailTuneTaskStatus;
import com.alipay.autotuneservice.util.DateUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author huangkaifei
 * @version : TrailTuneContext.java, v 0.1 2022年06月01日 3:05 PM huangkaifei Exp $
 */
@Data
public class TrailTuneContext {
    private Integer             appId;
    private Integer             pipelineId;
    private String              referPod;
    private String              trialPod;
    private TrialTuneMetric     referMetricFromAlgo;
    private TrialTuneMetric     trialMetricFromAlgo;
    private TrialTuneMetric     referMetricValue;
    private TrialTuneMetric     trialMetricValue;
    private TrailTuneTaskStatus taskStatus;
    private long                startTime;
    private long                endTime;

    public static TrailTuneContext convert(TuningParamTrialDataRecord record) {
        if (record == null) {
            return null;
        }
        try {
            TrailTuneContext trailTuneContext = new TrailTuneContext();
            int appId = StringUtils.isEmpty(record.getAppId()) ? 0 : Integer.parseInt(record
                .getAppId());
            trailTuneContext.setAppId(appId);
            trailTuneContext.setPipelineId(record.getPipelineId());
            trailTuneContext.setReferPod(record.getReferPods());
            trailTuneContext.setTrialPod(record.getTrialPods());
            trailTuneContext.setTaskStatus(TrailTuneTaskStatus.valueOf(record.getTaskStatus()));
            trailTuneContext.setReferMetricFromAlgo(JSON.parseObject(record.getReferMetricValue(),
                TrialTuneMetric.class));
            trailTuneContext.setTrialMetricFromAlgo(JSON.parseObject(record.getTrialMetricValue(),
                TrialTuneMetric.class));
            trailTuneContext.setStartTime(record.getStartTime() == null ? 0 : DateUtils
                .asTimestamp(record.getStartTime()));
            trailTuneContext.setEndTime(record.getStopTime() == null ? 0 : DateUtils
                .asTimestamp(record.getStopTime()));
            return trailTuneContext;
        } catch (Exception e) {
            // do nothing
            return null;
        }
    }
}
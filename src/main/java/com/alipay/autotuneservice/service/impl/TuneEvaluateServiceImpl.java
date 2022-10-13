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
package com.alipay.autotuneservice.service.impl;

import com.alipay.autotuneservice.model.common.EvaluateTypeEnum;
import com.alipay.autotuneservice.service.TuneEvaluateService;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author huoyuqi
 * @version TuneScoreServiceImpl.java, v 0.1 2022年04月24日 12:05 下午 huoyuqi
 */
@Slf4j
@Service
public class TuneEvaluateServiceImpl implements TuneEvaluateService {

    @Override
    public int evaluate(List<String> problemList) {
        try {
            if (CollectionUtils.isEmpty(problemList)) {
                return 100;
            }
            AtomicDouble count = new AtomicDouble();
            problemList.stream().filter(item -> EnumUtils.isValidEnum(EvaluateTypeEnum.class, item)).forEach(item1 ->
                    count.set(count.get() + EvaluateTypeEnum.valueOf(item1).getWeight()));
            if (count.get() == 0) {
                return 100;
            }
            int score = 100 - (int) Math.round(count.get() * 100);
            return problemList.size() == 1 ? score + 1 : score + 2;
        } catch (Exception e) {
            log.info("TuneEvaluateServiceImpl#evaluate occurs an error", e);
            return 75;
        }
    }

    @Override
    public Double divisionLevel(Long totalCount, Long count) {
        if (count == 0 || totalCount == 0) {
            return 0.0;
        }
        double weight = 100.0 / totalCount;
        return weight * count;
    }
}
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

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.controller.model.EffectTypeVO;
import com.alipay.autotuneservice.controller.model.TuneEffectVO;
import com.alipay.autotuneservice.model.common.EffectTypeEnum;
import com.alipay.autotuneservice.model.common.TuneStatus;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TuneEffectServiceImplTest {

    @Test
    public void predictTuneEffect() {
        TuneEffectVO tuneEffectVO = mockPredictData(0);
        System.out.println(JSON.toJSONString(tuneEffectVO));
    }

    private TuneEffectVO mockPredictData(Integer appId) {
        TuneEffectVO tuneEffectVO = new TuneEffectVO();
        tuneEffectVO.setPodNum(20);
        long now = System.currentTimeMillis();
        long startTime = now - 30 * 60 * 1000;
        long endTime = now + 30 * 60 * 1000;
        tuneEffectVO.setCheckStartTime(startTime);
        tuneEffectVO.setCheckEndTime(endTime);
        tuneEffectVO.setScore(92);
        tuneEffectVO.setPromoteRate(6.0);
        tuneEffectVO.setTuneOptimizePodNum(4);
        tuneEffectVO.setTotalIncome(531.88);
        tuneEffectVO.setTuneReduceCpu(20.0);
        tuneEffectVO.setTuneReduceMem(682.0);
        tuneEffectVO.setCurrentPodNum(20);
        List<EffectTypeVO> effectTypeVOList = new ArrayList<>();
        EffectTypeVO rtType = new EffectTypeVO();
        rtType.setEffectTypeEnum(EffectTypeEnum.RT.name());
        rtType.setReferResult(50.0);
        rtType.setTuneRate(0.06);
        rtType.setStatus(TuneStatus.OPTIMIZATION);
        effectTypeVOList.add(rtType);
        EffectTypeVO qpsType = new EffectTypeVO();
        qpsType.setEffectTypeEnum(EffectTypeEnum.QPS.name());
        qpsType.setReferResult(100.0);
        qpsType.setTuneRate(0.07);
        qpsType.setStatus(TuneStatus.OPTIMIZATION);
        effectTypeVOList.add(qpsType);
        EffectTypeVO heapType = new EffectTypeVO();
        heapType.setEffectTypeEnum(EffectTypeEnum.MEM.name());
        heapType.setReferResult(100.0);
        heapType.setTuneRate(0.05);
        heapType.setStatus(TuneStatus.WORSEN);
        effectTypeVOList.add(heapType);
        EffectTypeVO gcType = new EffectTypeVO();
        gcType.setEffectTypeEnum(EffectTypeEnum.FGC_TIME.name());
        gcType.setReferResult(100.0);
        gcType.setTuneRate(0.0);
        gcType.setStatus(TuneStatus.UNCHANGED);
        effectTypeVOList.add(gcType);
        EffectTypeVO gcCountType = new EffectTypeVO();
        gcCountType.setEffectTypeEnum(EffectTypeEnum.FGC_COUNT.name());
        gcCountType.setReferResult(30.0);
        gcCountType.setTuneRate(0.0);
        gcCountType.setStatus(TuneStatus.OPTIMIZATION);
        effectTypeVOList.add(gcCountType);
        tuneEffectVO.setTuneResultVOList(effectTypeVOList);
        return tuneEffectVO;
    }

    @Test
    public void tuneProcessEffect() {

    }

}
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
package com.alipay.autotuneservice.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.controller.model.configVO.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigInfoControllerTest {

    @Test
    public void operateConfig() {
        ConfigInfoVO vo = new ConfigInfoVO();
        vo.setAppId(34);
        vo.setAutoTune(true);

        Map<WeekEnum, List<TimeHHmm>> time = new HashMap<>();
        TimeHHmm hh = new TimeHHmm();
        hh.setStart("18:00");
        hh.setEnd("20:00");

        TimeHHmm ww = new TimeHHmm();
        ww.setStart("09:00");
        ww.setEnd("10:00");
        time.put(WeekEnum.MON, new ArrayList() {
            {
                add(hh);
                add(ww);
            }
        });
        TimeHHmm eerr = new TimeHHmm();
        eerr.setStart("09:00");
        eerr.setEnd("10:00");
        time.put(WeekEnum.TUE, new ArrayList() {
            {
                add(eerr);
            }
        });
        time.put(WeekEnum.WED, new ArrayList());
        time.put(WeekEnum.THU, new ArrayList() {
            {

            }
        });
        time.put(WeekEnum.FRI, new ArrayList());
        time.put(WeekEnum.SAT, new ArrayList() {
            {

            }
        });
        TimeHHmm qq = new TimeHHmm();
        qq.setStart("09:00");
        qq.setEnd("10:00");

        TimeHHmm qwe = new TimeHHmm();
        qwe.setStart("09:00");
        qwe.setEnd("10:00");

        TimeHHmm rdr = new TimeHHmm();
        rdr.setStart("09:00");
        rdr.setEnd("10:00");
        time.put(WeekEnum.SUN, new ArrayList() {
            {
                add(qq);
                add(qwe);
                add(rdr);
            }
        });
        vo.setTunePrimaryTime(time);

        vo.setAutoDispatch(true);
        TuneConfig tuneConfig = new TuneConfig();
        tuneConfig.setNumber(1);
        tuneConfig.setPercent(0.3);

        TuneConfig tuneConfig2 = new TuneConfig();
        tuneConfig2.setNumber(2);
        tuneConfig2.setPercent(0.8);
        vo.setTuneGroupConfig(new ArrayList() {
            {
                add(tuneConfig);
                add(tuneConfig2);
            }
        });

        vo.setRiskSwitch(true);
        RiskIndictor riskIndictor = new RiskIndictor();
        riskIndictor.setIndictor("xx");
        riskIndictor.setOnOFF(true);
        riskIndictor.setMin(0.2);
        riskIndictor.setMax(0.5);

        RiskIndictor riskIndictorf = new RiskIndictor();
        riskIndictorf.setIndictor("xx");
        riskIndictorf.setOnOFF(false);
        riskIndictorf.setMin(1.0);
        riskIndictorf.setMax(4.0);
        vo.setAdvancedSetup(new ArrayList() {
            {
                add(riskIndictor);
                add(riskIndictorf);
            }
        });

        System.out.println(JSON.toJSONString(vo));
    }
}

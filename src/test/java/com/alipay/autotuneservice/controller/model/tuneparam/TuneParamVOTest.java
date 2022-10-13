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
package com.alipay.autotuneservice.controller.model.tuneparam;

import com.alibaba.fastjson.JSON;
import com.alipay.autotuneservice.model.ServiceBaseResult;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TuneParamVOTest {

    @Test
    public void testPrintCurrentParam() {
        //  -XX:MaxPermSize=60M,
        TuneParamVO tuneParamVO = new TuneParamVO();
        tuneParamVO.setParamName("xx");
        tuneParamVO.setOperator("xx");
        tuneParamVO.setCurrentVal("xx");

        String s = tuneParamVO.getTuneParam();
        System.out.println(s);
        assertTrue(StringUtils.equals(s, "xx"));

        // -XX:+UseSerialGC
        TuneParamVO tuneParamVO1 = new TuneParamVO();
        tuneParamVO1.setParamName("xx");
        tuneParamVO1.setOperator("xx");
        tuneParamVO1.setCurrentVal("xx");
        tuneParamVO1.setOriginVal("xx");

        String s1 = tuneParamVO1.getOriginParam();
        System.out.println(s1);
        assertTrue(StringUtils.equals(s1, "xx"));
        List<TuneParamVO> tuneList = new ArrayList<>();
        tuneList.add(tuneParamVO);

        List<TuneParamVO> addList = new ArrayList<>();
        addList.add(tuneParamVO1);
        AppTuneParamsVO appTuneParamsVO = new AppTuneParamsVO();
        appTuneParamsVO.setAppId(82);
        appTuneParamsVO.setAutoTune(true);
        String s2 = JSON.toJSONString(appTuneParamsVO);
        System.out.println(s2);

        ServiceBaseResult<AppTuneParamsVO> appTuneParamsVOServiceBaseResult = ServiceBaseResult
            .successResult(appTuneParamsVO);
        System.out.println(JSON.toJSONString(appTuneParamsVOServiceBaseResult));
    }
}
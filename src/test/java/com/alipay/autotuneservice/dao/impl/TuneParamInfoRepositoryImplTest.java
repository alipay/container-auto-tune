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
package com.alipay.autotuneservice.dao.impl;

import com.alipay.autotuneservice.dao.TuneParamInfoRepository;
import com.alipay.autotuneservice.dao.jooq.tables.records.TuneParamInfoRecord;
import com.alipay.autotuneservice.model.tune.params.TuneParamUpdateStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TuneParamInfoRepositoryImplTest {

    @Autowired
    private TuneParamInfoRepository tuneParamInfoRepository;

    @Test
    public void insert() {
        TuneParamInfoRecord record = new TuneParamInfoRecord();
        record.setAppId(912);
        record.setPipelineId(20);
        record.setUpdateParams("{}");
        record.setJvmMarketId(25);
    }

    @Test
    public void findTunableTuneParamRecord() {
        try {
            TuneParamInfoRecord tunableTuneParamRecord = tuneParamInfoRepository
                .findTunableTuneParamRecord(0, 0);
            System.out.println("________________________________" + tunableTuneParamRecord.getId());
            System.out.println(tunableTuneParamRecord.getUpdateStatus());
        } catch (Exception e) {
            System.out.println("出错了------------");
        }

        TuneParamInfoRecord tunableTuneParamRecord = tuneParamInfoRepository
            .findTunableTuneParamRecord(0, 0);
        System.out.println("________________________________" + tunableTuneParamRecord.getId());
        System.out.println(tunableTuneParamRecord.getUpdateStatus());
    }

    @Test
    public void selectByAppId(){
        List<TuneParamInfoRecord> tuneParamInfoRecordList = tuneParamInfoRepository.getByAppId(0);
        tuneParamInfoRecordList.forEach(r-> System.out.println(r.getPipelineId()));
    }

    @Test
    public void findByAppId() {
        TuneParamInfoRecord tuneParamInfoRecord = tuneParamInfoRepository.findByAppId(3);
        System.out.println(tuneParamInfoRecord.getId());
    }
}
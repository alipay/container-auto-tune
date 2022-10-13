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
package com.alipay.autotuneservice.tunepool;

import com.alibaba.fastjson.JSONObject;
import com.alipay.autotuneservice.model.tunepool.MetaData;
import com.alipay.autotuneservice.model.tunepool.TuneEntity;
import com.alipay.autotuneservice.model.tunepool.TunePoolStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * @author chenqu
 * @version : TunePoolTest.java, v 0.1 2022年03月31日 10:46 chenqu Exp $
 */
@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.main.lazy-initialization=true")
@DisplayName("Redis Cache Service Unit Tests")
public class TunePoolTest {

    @Autowired
    private TuneProcessor tuneProcessor;

    @Test
    public void getSourceTest() {
        //获取资源句柄
        TuneSource tuneSource = tuneProcessor.getTuneSource(generateEntity());
        //获取分批池
        System.out.println(JSONObject.toJSONString(tuneSource.batchTunePool()));
        System.out.println(JSONObject.toJSONString(tuneSource.experimentTunePool()));
    }

    @Test
    public void registerBatchTunePoolTest() {
        //修改元数据并刷新
        CountDownLatch downLatch = new CountDownLatch(5);
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                //获取资源句柄
                TuneSource tuneSource = tuneProcessor.getTuneSource(generateEntity());
                tuneSource.experimentTunePool().getTuneMeta();
                //获取分批池
                TunePool tunePool = tuneSource.batchTunePool();
                System.out.println("xx" + tunePool.getPoolStatus());
                int count = new Random().nextInt(0);
                MetaData metaData = tunePool.getTuneMeta();
                metaData.setReplicas(count);
                metaData.setJvmMarketId(0L);
                tunePool.registerTuneMeta(metaData)
                        .moveStatus(TunePoolStatus.RUNNABLE)
                        .refresh();
                System.out.println(Thread.currentThread().getId() + ":" + count);
                downLatch.countDown();
            }).start();
        }
        try {
            downLatch.await();
            //输出
            TuneSource tuneSource = tuneProcessor.getTuneSource(generateEntity());
            TunePool tunePool = tuneSource.batchTunePool();
            System.out.println("xx" + tunePool.getPoolStatus());
            System.out.println(JSONObject.toJSONString(tunePool));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateBatchTunePoolTest() {
        //获取资源句柄
        TuneSource tuneSource = tuneProcessor.getTuneSource(generateEntity());
        //获取分批池
        TunePool tunePool = tuneSource.batchTunePool();
        System.out.println("xx" + tunePool.getPoolStatus());
        //修改元数据并刷新
        MetaData metaData = tunePool.getTuneMeta();
        metaData.setReplicas(0);
        metaData.setJvmMarketId(0L);
        tunePool.updateTuneMeta(metaData).moveStatus(TunePoolStatus.DELETE).refresh();
        System.out.println("xx" + tunePool.getPoolStatus());
        System.out.println(JSONObject.toJSONString(tunePool));
        TuneSource tuneSource2 = tuneProcessor.getTuneSource(generateEntity());
        System.out.println(tuneSource2.batchTunePool().getPoolStatus());
    }

    @Test
    public void registerExperimentTunePoolTest() {
        //获取资源句柄
        TuneSource tuneSource = tuneProcessor.getTuneSource(generateEntity());
        //获取分批池
        TunePool tunePool = tuneSource.experimentTunePool();
        System.out.println("xx" + tunePool.getPoolStatus());
        ////修改元数据并刷新
        //MetaData metaData = tunePool.getTuneMeta();
        //metaData.setReplicas(0);
        //metaData.setJvmMarketId(0L);
        //tunePool.registerTuneMeta(metaData)
        //        .moveStatus(TunePoolStatus.RUNNABLE)
        //        .refresh();
        //System.out.println("status:" + tunePool.getPoolStatus());
        System.out.println(JSONObject.toJSONString(tunePool.getTuneMeta()));
    }

    private TuneEntity generateEntity() {
        return TuneEntity.builder().appId(0).pipelineId(0).build();
    }

}
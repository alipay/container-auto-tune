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
package com.alipay.autotuneservice.service.pipeline;

import com.alipay.autotuneservice.dao.TunePipelineRepository;
import com.alipay.autotuneservice.message.TuneMessageBroker;
import com.alipay.autotuneservice.message.TuneMessageEvent;
import com.alipay.autotuneservice.message.TuneMessageEventListener;
import com.alipay.autotuneservice.model.pipeline.Status;
import com.alipay.autotuneservice.model.pipeline.TuneEvent;
import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dutianze
 * @version TuneEventProducer.java, v 0.1 2022年04月06日 15:33 dutianze
 */
@Slf4j
@Component
public class TuneEventProducer implements InitializingBean {

    @Autowired
    private TuneFlowService          tuneFlowService;
    @Autowired
    private TunePipelineRepository   pipelineRepository;
    @Autowired
    private TuneMessageBroker        messageBroker;
    @Autowired
    private TuneMessageEventListener tuneMessageListener;

    private static final String CONSUMER_KEY = "autotune_TuneEventProducer_lock";

    public void send(TuneEvent tuneEvent) {
        try {
            //redisClient.publish(tuneEvent);
            messageBroker.pub(new TuneMessageEvent(this, tuneEvent));
        } catch (Exception e) {
            log.error("TuneEventProducer send error", e);
        }
    }

    private void processEvent(TuneEvent tuneEvent) {
        log.info("processEvent, tuneEvent:{}", tuneEvent);
        TunePipeline tunePipeline = null;
        try {
            tunePipeline = pipelineRepository.findByPipelineIdAndStatus(tuneEvent.getPipelineId(), Status.RUNNING);
        } catch (Exception e) {
            log.warn("processEvent occurs an error,  tuneEvent:{}", tuneEvent);
        }
        if (tunePipeline == null) {
            log.warn("processEvent, tunePipeline is not fount, tuneEvent:{}", tuneEvent);
            return;
        }
        tuneFlowService.fireTask(tunePipeline, tuneEvent);
    }

    @Override
    public void afterPropertiesSet() {
        tuneMessageListener.subscribe(event -> processEvent((TuneEvent) event.getTuneEvent()));
    }
}
/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.service.pipeline;

import com.alipay.autotuneservice.dao.TunePipelineRepository;
import com.alipay.autotuneservice.infrastructure.saas.common.cache.RedisClient;
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
    private TuneFlowService        tuneFlowService;
    @Autowired
    private TunePipelineRepository pipelineRepository;
    @Autowired
    private RedisClient            redisClient;
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
        //redisClient.subscribe(event -> {
        //    try {
        //        log.info("TuneEventProducer consumer, event:{}", event);
        //        ObjectUtil.tryLock(redisClient, CONSUMER_KEY + event.getPipelineId() + event.getEventType().name(), 0, new AbsLockAction() {
        //            @Override
        //            public void doInLock(String resourceName) {
        //                try {
        //                    processEvent(event);
        //                } catch (Exception e) {
        //                    log.error("TuneEventProducer processEvent error", e);
        //                }
        //
        //            }
        //
        //            @Override
        //            public void tryLockFail(String resourceName) {
        //                log.info("TuneEventProducer tryLockFail, event:{}", event);
        //            }
        //        });
        //    } catch (Exception e) {
        //        log.error("TuneEventProducer consumer error", e);
        //    }
        //
        //}, TuneEvent.class);
    }
}
/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * @author huangkaifei
 * @version : TuneMessageBroker.java, v 0.1 2022年11月08日 7:42 PM huangkaifei Exp $
 */
@Slf4j
@Component
public class TuneMessageBroker implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void pub(TuneMessageEvent event){
        log.info("TuneMessageBroker start to pub event");
        applicationEventPublisher.publishEvent(event);
    }
}
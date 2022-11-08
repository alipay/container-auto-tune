/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * @author huangkaifei
 * @version : TuneMessageEventListener.java, v 0.1 2022年11月08日 7:25 PM huangkaifei Exp $
 */
@Component
@Slf4j
public class TuneMessageEventListener implements ApplicationListener<TuneMessageEvent> {

    private Consumer<TuneMessageEvent> consumer;

    public void subscribe(Consumer<TuneMessageEvent> consumer){
        this.consumer = consumer;
    }

    @Override
    public void onApplicationEvent(TuneMessageEvent event) {
        log.info("TuneMessageEventListener start to do action...");
        consumer.accept(event);
    }
}
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
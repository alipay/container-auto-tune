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
package com.alipay.autotuneservice.tunerx.watcher;

import com.alipay.autotuneservice.model.pipeline.TunePipeline;
import com.alipay.autotuneservice.model.pipeline.TuneStage;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;

/**
 * @author chenqu
 * @version : EventPipelineFactory.java, v 0.1 2021年07月30日 14:30 chenqu Exp $
 */
@Service
@Slf4j
public class EventCheckerFactory implements ApplicationContextAware {

    private final Map<TuneStage, Class<? extends EventChecker>> resources = Maps.newConcurrentMap();
    private ApplicationContext                                  applicationContext;

    /**
     * 初始化注册器
     */
    @PostConstruct
    protected void register() {
        Reflections reflections = new Reflections("com", new SubTypesScanner(true));
        Set<Class<? extends EventChecker>> subTypes = reflections.getSubTypesOf(EventChecker.class);
        for (Class<? extends EventChecker> checkerClass : subTypes) {
            try {
                Constructor con = checkerClass.getConstructor(ApplicationContext.class,
                    TunePipeline.class);
                EventChecker eventChecker = (EventChecker) con.newInstance(null, null);
                resources.put(eventChecker.tuneStage(), eventChecker.getClass());
            } catch (Exception e) {
                log.error("register is error", e);
            }
        }

    }

    EventChecker findChecker(TunePipeline tunePipeline) {
        try {
            log.info("findChecker begin!");
            if (!resources.containsKey(tunePipeline.getStage())) {
                throw new RuntimeException(String.format("not found checker by tunePipeline=[%s]",
                    tunePipeline.getStage().name()));
            }
            Class<? extends EventChecker> clazz = resources.get(tunePipeline.getStage());
            Constructor<? extends EventChecker> con = clazz.getConstructor(
                ApplicationContext.class, TunePipeline.class);
            return con.newInstance(applicationContext, tunePipeline);
        } catch (Exception e) {
            log.error("findChecker is error", e);
            return null;
        }
    }

    Set<TuneStage> getTuneStages() {
        return resources.keySet();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
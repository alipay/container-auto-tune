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

import com.alipay.autotuneservice.model.tunepool.TuneEntity;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @author chenqu
 * @version : TuneProcessor.java, v 0.1 2022年03月29日 14:19 chenqu Exp $
 */
@Service
public class TuneProcessor implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public TuneSource getTuneSource(TuneEntity tuneEntity) {
        return TuneSource.builder().context(applicationContext).tuneEntity(tuneEntity).build();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //赋值
        this.applicationContext = applicationContext;
    }
}
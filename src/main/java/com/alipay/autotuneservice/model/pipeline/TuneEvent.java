/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.autotuneservice.model.pipeline;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * TODO check pipeline is cooled or burned
 * <p>
 * TODO refuse burned pipeline
 * <p>
 * TODO pipeline执行幂等
 *
 * @author dutianze
 * @version TunePipelineEvent.java, v 0.1 2022年04月06日 14:37 dutianze
 */
@Data
@NoArgsConstructor
public class TuneEvent implements Serializable {

    private Integer id;

    private boolean consumed;

    private Integer pipelineId;

    private TuneEventType eventType;

    private TuneContext context;

    public TuneEvent(Integer pipelineId, TuneEventType eventType) {
        this.pipelineId = pipelineId;
        this.eventType = eventType;
    }
}
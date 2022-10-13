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
package com.alipay.autotuneservice.controller.model;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * @author huoyuqi
 * @version TuneResultVO.java, v 0.1 2022年04月27日 4:20 下午 huoyuqi
 */
@Data
public class TuneTestTimePipeVO {

    /**
     * 开始时间
     */
    private long             startTime;

    /**
     * 开始时间
     */
    private long             finishTime;

    /**
     * 参照检查开始时间
     */
    private boolean          isFinish     = Boolean.FALSE;

    /**
     * 时间序列
     */
    private List<TimeDetail> timePipeline = Lists.newLinkedList();

    @Data
    public static class TimeDetail {

        /**
         * 开始时间
         */
        private long   startTime;

        /**
         * 结束时间
         */
        private long   endTime;

        /**
         * pod名称
         */
        private String podName;
    }
}
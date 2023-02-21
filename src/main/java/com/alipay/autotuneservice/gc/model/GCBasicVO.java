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
package com.alipay.autotuneservice.gc.model;

import lombok.Data;

/**
 * @author huoyuqi
 * @version GCBasicVO.java, v 0.1 2022年11月04日 7:03 下午 huoyuqi
 */
@Data
public class GCBasicVO {

    private String fileName;

    private String collector;

    private Long timeStamp;

    private Long startTime;

    private Long endTime;

    private Long duration;

    private Long searchStartTime;

    private Long searchEndTime;

    private Long searchDuration;

    public GCBasicVO(String fileName, String collector, Long timeStamp, Long startTime, Long endTime, Long duration, Long searchStartTime,
                     Long searchEndTime) {
        this.fileName = fileName;
        this.collector = collector;
        this.timeStamp = timeStamp;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        if (searchStartTime == null && searchEndTime == null) {
            this.searchStartTime = this.startTime;
            this.searchEndTime = this.endTime;
            this.searchDuration = this.duration;
        }
        if (searchStartTime != null && searchEndTime != null) {
            this.searchStartTime = searchStartTime;
            this.searchEndTime = searchEndTime;
            this.searchDuration = searchEndTime - searchStartTime;
        }
    }

}
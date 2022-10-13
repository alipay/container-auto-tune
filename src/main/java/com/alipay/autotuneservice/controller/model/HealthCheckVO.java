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

import com.alipay.autotuneservice.model.common.HealthCheckStatus;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * @author huoyuqi
 * @version HealthCheckVO.java, v 0.1 2022年04月27日 4:00 下午 huoyuqi
 */
@Data
public class HealthCheckVO {

    /**
     * 健康项检查id
     */
    private int                          id;

    /**
     * 应用名称id
     */
    private int                          appId;

    /**
     * 健康检查项分数
     */
    private int                          score;

    /**
     * 健康检查异常项数
     */
    private int                          checkNum;

    /**
     * 八项检查内容展示 主要是调优界面展示正常状态，
     */
    private List<HealthCheckStatusModel> checkStatusList = Lists.newArrayList();

    /**
     * 检测完成数量
     */
    private int                          checkedNum;

    /**
     * 参照检查开始时间
     */
    private long                         checkStartTime;

    /**
     * 参照检查结束时间
     */
    private Long                         checkEndTime;

    /**
     * 检测时间
     */
    private long                         checkTime;

    /**
     * 状态
     */
    private HealthCheckStatus            status;

    /**
     * 需要确认的流程id
     */
    private Integer                      confirmPipelineId;

    @Data
    public static class HealthCheckStatusModel {

        /**
         * 问题名称
         */
        private String     type;

        /**
         * 问题状态
         */
        private String     status;

        /**
         * 结论
         */
        private String     conclusion;

        /**
         * 问题详情
         */
        private String     problemDetail;

        /**
         * 问题时间点
         */
        private List<Long> problemTime;
    }
}

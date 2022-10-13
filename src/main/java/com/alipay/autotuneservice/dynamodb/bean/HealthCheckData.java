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
package com.alipay.autotuneservice.dynamodb.bean;

import lombok.Data;

/**
 * 获取健康检查结果
 *
 * @version HealthCheckData.java, v 0.1 2022年02月21日 7:10 下午 quchen
 */
@Data
public class HealthCheckData {

    private String dt;
    private long   app_id;
    private String app;
    private String jvm_problem;
    private String jvm_state;
    private String mode;
    private String suggest;
    private String timestamp;

    /**
     *  {@link com.alipay.autotuneservice.schedule.riskstatistic.RiskStatisticCaType }
     */
    private int    type;

    public String getDt() {
        return dt;
    }

    public long getApp_id() {
        return app_id;
    }

    public HealthCheckData bdDt(String dt) {
        this.dt = dt;
        return this;
    }

    public HealthCheckData bdApp_id(long app_id) {
        this.app_id = app_id;
        return this;
    }

    public HealthCheckData bdApp(String app) {
        this.app = app;
        return this;
    }

    public HealthCheckData bdJvm_problem(String jvm_problem) {
        this.jvm_problem = jvm_problem;
        return this;
    }

    public HealthCheckData bdJvm_state(String jvm_state) {
        this.jvm_state = jvm_state;
        return this;
    }

    public HealthCheckData bdMode(String mode) {
        this.mode = mode;
        return this;
    }

    public HealthCheckData bdSuggest(String suggest) {
        this.suggest = suggest;
        return this;
    }

    public HealthCheckData bdTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public HealthCheckData bdType(int type) {
        this.type = type;
        return this;
    }

    public static HealthCheckData newInstance() {
        return new HealthCheckData();
    }
}
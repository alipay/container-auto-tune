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
package com.alipay.autotuneservice.meter.model;

import org.apache.commons.lang3.StringUtils;

/**
 * @author huangkaifei
 * @version : MeterType.java, v 0.1 2022年08月19日 2:48 PM huangkaifei Exp $
 */
public enum MeterType {
    PROMETHEUS("prometheus", "prometheus monitor"),
    DATADOG("datadog", "datadog")
    ;

    private String name;
    private String desc;

    MeterType(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public static MeterType find(String meterName){
        for (MeterType meterType: values()){
            if (StringUtils.equalsIgnoreCase(meterType.name, meterName)) {
                return meterType;
            }
        }
        throw new UnsupportedOperationException(String.format("meterName:%s is not supported now", meterName));
    }
}
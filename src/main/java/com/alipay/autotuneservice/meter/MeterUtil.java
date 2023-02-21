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
package com.alipay.autotuneservice.meter;

import com.alipay.autotuneservice.controller.model.meter.MeterMeta;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author huangkaifei
 * @version : MeterUtil.java, v 0.1 2022年09月18日 11:16 PM huangkaifei Exp $
 */
public class MeterUtil {

    public static String getMeterScheme(MeterMeta meterMeta){
        meterMeta = Objects.requireNonNull(meterMeta, "meterMeta can not be null.");
        if (StringUtils.startsWith((meterMeta.getMeterDomain()), "https://")) {
            return "https";
        }
        return "http";
    }

    public static String getMeterDomain(MeterMeta meterMeta){
        meterMeta = Objects.requireNonNull(meterMeta, "meterMeta can not be null.");
        String meterDomain = meterMeta.getMeterDomain();
        String[] split = meterDomain.split("//");
        if (split.length == 1) {
            return meterDomain;
        }
        if (split.length == 2) {
            return split[1];
        }
        throw new UnsupportedOperationException("Input MeterDomain not supported.");
    }
}
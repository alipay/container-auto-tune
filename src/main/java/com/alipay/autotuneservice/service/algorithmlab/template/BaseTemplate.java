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
package com.alipay.autotuneservice.service.algorithmlab.template;

import com.alipay.autotuneservice.service.algorithmlab.GarbageCollector;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hongshu
 * @version TuneLab.java, v 0.1 2022年11月17日 21:15 hongshu
 */
public class BaseTemplate {

    private static final String CMS_BASE = "-XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSClassUnloadingEnabled -XX:+UseCMSInitiatingOccupancyOnly " +
            "-XX:CMSInitiatingOccupancyFraction=65 -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps";

    private static final String G1_BASE = "-XX:+UseG1GC  -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps";

    // 默认模版
    public static final Map<GarbageCollector,Map<String, String>> TEMP_DEFAULT = new HashMap<GarbageCollector,Map<String, String>>(){{
        put(GarbageCollector.CMS_GARBAGE_COLLECTOR,new HashMap<String, String>(){{
            put("4",CMS_BASE + " " + "-Xmx2000m -Xms2000m -XX:NewSize=1000m -XX:MaxNewSize=1000m -Xss512k -XX:MaxMetaspaceSize=500M -XX:MetaspaceSize=500M -XX:InitiatingHeapOccupancyPercent=50");
            put("8",CMS_BASE + " " + "-Xmx5000m -Xms5000m -XX:NewSize=2000m -XX:MaxNewSize=2000m -Xss1m -XX:MaxMetaspaceSize=750M -XX:MetaspaceSize=750M -XX:InitiatingHeapOccupancyPercent=65");
            put("12",CMS_BASE + " " + "-Xmx8000m -Xms8000m -XX:NewSize=3000m -XX:MaxNewSize=3000m -Xss1m -XX:MaxMetaspaceSize=1000M -XX:MetaspaceSize=1000M -XX:InitiatingHeapOccupancyPercent=65");
            put("16",CMS_BASE + " " + "-Xmx12000m -Xms12000m -XX:NewSize=6000m -XX:MaxNewSize=6000m -Xss1m -XX:MaxMetaspaceSize=1000M -XX:MetaspaceSize=1000M -XX:InitiatingHeapOccupancyPercent=65");
            put("32",CMS_BASE + " " + "-Xmx24000m -Xms24000m -XX:NewSize=10000m -XX:MaxNewSize=10000m -Xss1m -XX:MaxMetaspaceSize=1000M -XX:MetaspaceSize=1000M -XX:InitiatingHeapOccupancyPercent=65");
        }});
        put(GarbageCollector.G1_GARBAGE_COLLECTOR,new HashMap<String, String>(){{
            put("4",G1_BASE + " " + "-Xmx2g -Xms2g -XX:MaxNewSize=1g -XX:InitiatingHeapOccupancyPercent=50 -Xss512k -XX:MaxMetaspaceSize=512m -XX:MetaspaceSize=512m");
            put("8",G1_BASE + " " + "-Xmx5g -Xms5g -XX:MaxNewSize=2g -XX:InitiatingHeapOccupancyPercent=50 -Xss512k -XX:MaxMetaspaceSize=512m -XX:MetaspaceSize=512m");
            put("12",G1_BASE + " " + "-Xmx8g -Xms8g -XX:MaxNewSize=3g -XX:InitiatingHeapOccupancyPercent=55 -Xss1m -XX:MaxMetaspaceSize=512m -XX:MetaspaceSize=512m");
            put("16",G1_BASE + " " + "-Xmx12g -Xms12g -XX:MaxNewSize=6g -XX:InitiatingHeapOccupancyPercent=60 -Xss1m -XX:MaxMetaspaceSize=1000m -XX:MetaspaceSize=1000m");
            put("32",G1_BASE + " " + "-Xmx24g -Xms24g -XX:MaxNewSize=12g -XX:InitiatingHeapOccupancyPercent=60 -Xss1m -XX:MaxMetaspaceSize=1000m -XX:MetaspaceSize=1000m");
        }});
    }};
}

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

import static org.eclipse.jifa.gclog.util.Constant.UNKNOWN_DOUBLE;
import static org.eclipse.jifa.gclog.util.Constant.UNKNOWN_INT;

/**
 * @author huoyuqi
 * @version GCObject.java, v 0.1 2022年11月09日 12:23 下午 huoyuqi
 */
@Data
public class GCObject {

    /**
     * object 创建速度
     */
    private double objectCreationSpeed = UNKNOWN_DOUBLE;

    /**
     * object 晋升速度
     */
    private double objectPromotionSpeed = UNKNOWN_DOUBLE;

    /**
     * object 晋升对象平均大小
     */
    private long objectPromotionAvg = UNKNOWN_INT;

    /**
     * object 最大单次晋升大小
     */
    private long objectPromotionMax = UNKNOWN_INT;

    /**
     * 吞吐量
     */
    private double throughPut;

    public GCObject(double objectCreationSpeed, double objectPromotionSpeed, long objectPromotionAvg, long objectPromotionMax,
                    double throughPut) {
        this.objectCreationSpeed = objectCreationSpeed / 1024;
        this.objectPromotionSpeed = objectPromotionSpeed / 1024;
        this.objectPromotionAvg = objectPromotionAvg / 1024 / 1024;
        this.objectPromotionMax = objectPromotionMax / 1024 / 1024;
        this.throughPut = throughPut;
    }

}
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
package com.alipay.autotuneservice.model.tune.params;

/**
 * @author huangkaifei
 * @version : TuneParamUpdateStatus.java, v 0.1 2022年05月18日 2:42 PM huangkaifei Exp $
 */
public enum TuneParamUpdateStatus {
    /**
     * 初始化
     */
    INIT,
    /**
     * 更新中
     */
    RUNNING,
    /**
     * 更新完成
     */
    END,
    /**
     * 更新出现异常
     */
    EXCEPTION;

    public static TuneParamUpdateStatus findByName(String status) {
        for (TuneParamUpdateStatus updateStatus : values()) {
            if (updateStatus.name().equals(status)) {
                return updateStatus;
            }
        }
        throw new UnsupportedOperationException(String.format("status=%s is unsupported.", status));
    }
}
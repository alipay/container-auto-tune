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
package com.alipay.autotuneservice.model.tunepool;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * @author chenqu
 * @version : MetaData.java, v 0.1 2022年03月29日 14:29 chenqu Exp $
 */
@Data
@NoArgsConstructor
public class MetaData implements Serializable {

    private long   jvmMarketId;
    private String jvmCmd;
    private Type   type = Type.NUMBER;
    private long   replicas;
    /**
     * 元数据描述:分批用于存放分批批次;实验用于存放实验标识
     */
    private String desc = "1";

    public enum Type {
        NUMBER,
        //比例情况下,replicas不能高于100
        RATIO;
    }

    public MetaData copy() {
        MetaData metaData = new MetaData();
        BeanUtils.copyProperties(this, metaData);
        return metaData;
    }
}
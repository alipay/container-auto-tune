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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 调参最终一致性入参
 *
 * @author chenqu
 * @version : TuneConsistencyRq.java, v 0.1 2022年04月05日 19:43 chenqu Exp $
 */
@Data
public class TuneConsistencyRq {

    private Integer                     pipelineId;
    private String                      accessToken;
    private Integer                     appId;
    private String                      appName;
    private PoolType                    poolType;
    private Map<MetaDataType, MetaData> metaDataMap = Maps.newHashMap();
    private List<ChangeRq>              changeRqs   = Collections.synchronizedList(Lists
                                                        .newArrayList());

    public boolean checkEmpty() {
        if (checkStrEmpty(accessToken) || checkStrEmpty(appName)) {
            return Boolean.TRUE;
        }
        if (metaDataMap.isEmpty() || metaDataMap.size() < 2) {
            return Boolean.TRUE;
        }
        if (poolType == null) {
            return Boolean.TRUE;
        }
        return appId == null;
    }

    public boolean checkStrEmpty(String str) {
        return StringUtils.isEmpty(str);
    }

    public String generateUnionKey() {
        return String.format("consistency_%s_%s", this.appId, this.poolType.name());
    }

    @Data
    public static class ChangeRq {
        private Integer      pipelineId;
        private String       accessToken;
        private Integer      appId;
        private String       appName;
        private PoolType     poolType;
        private MetaDataType metaDataType;
        private MetaData     metaData;
        private Long         changeNum;
        //默认是1
        private Integer      restartMaxNum = 1;

        public String generateUnionKey() {
            return String.format("consistency_%s_%s", this.appId, this.poolType.name());
        }
    }
}
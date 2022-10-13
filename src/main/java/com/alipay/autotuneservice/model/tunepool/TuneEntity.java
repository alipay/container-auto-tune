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

import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @author chenqu
 * @version : TuneEntity.java, v 0.1 2022年03月29日 15:45 chenqu Exp $
 */
@Builder
@Getter
public class TuneEntity {

    @NotNull
    private Integer appId;
    @NotNull
    private String  accessToken;
    @NotNull
    private Integer pipelineId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TuneEntity that = (TuneEntity) o;
        return Objects.equals(appId, that.appId) && Objects.equals(accessToken, that.accessToken)
               && Objects.equals(pipelineId, that.pipelineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appId, accessToken, pipelineId);
    }

    public boolean checkEmpty() {
        return StringUtils.isEmpty(accessToken) || appId == null || pipelineId == null;
    }

    @Override
    public String toString() {
        return "TuneEntity{" + "appId=" + appId + ", accessToken='" + accessToken + '\''
               + ", pipelineId=" + pipelineId + '}';
    }
}
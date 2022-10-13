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
package com.alipay.autotuneservice.controller.model.tuneplan;

import com.alipay.autotuneservice.controller.model.TunePlanVO;
import com.alipay.autotuneservice.model.tune.TunePlanStatus;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author huangkaifei
 * @version : QueryTunePlanVO.java, v 0.1 2022年05月17日 10:34 AM huangkaifei Exp $
 */
@Data
public class QueryTunePlanVO {
    /**
     * 所有tune plan
     **/
    private Integer          allNums      = 0;
    /**
     * 所有 异常 tune plan
     **/
    private Integer          unUsualNums  = 0;
    /**
     * 所有 已完成 tune plan
     **/
    private Integer          finishedNums = 0;
    /**
     * 符合查询条件的 tune plan list
     */
    private List<TunePlanVO> tunePlanList;

    public void buildTunePlanNums() {
        if (CollectionUtils.isEmpty(tunePlanList)) {
            return;
        }
        this.allNums = tunePlanList.size();
        this.unUsualNums = (int) tunePlanList.stream().filter(Objects::nonNull)
                .filter(item -> item.getTunePlanStatus() == TunePlanStatus.EXCEPTION).count();
        this.finishedNums = (int) tunePlanList.stream().filter(Objects::nonNull)
                .filter(item -> item.getTunePlanStatus() == TunePlanStatus.END).count();
    }

    public void buildTunePlanList(TunePlanStatus status){
        if (status == null) {
            return;
        }
        if (CollectionUtils.isEmpty(tunePlanList)) {
            return;
        }
        this.tunePlanList = tunePlanList.stream().filter(item -> item!=null && status == item.getTunePlanStatus()).collect(Collectors.toList());
    }
}
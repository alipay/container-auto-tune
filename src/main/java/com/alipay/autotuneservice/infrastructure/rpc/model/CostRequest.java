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
package com.alipay.autotuneservice.infrastructure.rpc.model;

import com.alipay.autotuneservice.util.DateUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dutianze
 * @version CostRequest.java, v 0.1 2022年05月18日 16:30 dutianze
 */
@Data
@NoArgsConstructor
public class CostRequest {

    // 自定义订单号
    private String  bizOrderNo;

    // 需要业务方计量记录绑定产品账户
    private String  productAccountId;

    // 消费用户，不传默认展示system
    private String  consumer;

    // 产品码（saas工厂提供）
    private String  productCode;

    // 租户码（saas工厂提供）
    private String  tenantCode;

    // 维度类型，枚举：request/storage/agent/cpucore
    private String  dimType;

    // 维度名称
    private String  dimName;

    // 维度值
    private Integer dimVal;

    // 计量方式，async异步 sync同步
    private String  metricWay;

    // 同步必须返回该值   1有效 2无效
    private Integer status;

    // 相关附加解释（可选）
    private String  description;

    // 产品方案码（saas工厂提供）代表是免费体验版还是商业版
    private String  planCode;

    public CostRequest(CostCell costCell, String productCode, int agentInstallCount) {
        this.bizOrderNo = generateBizOrderNo(costCell);
        this.productAccountId = costCell.getProductAccountId();
        this.consumer = "system";
        this.productCode = productCode;
        this.tenantCode = costCell.getTenantCode();
        this.dimType = "agent";
        this.dimName = "安装agent数量";
        this.dimVal = agentInstallCount;
        this.metricWay = "sync";
        this.status = 1;
        this.description = DateUtils.of(DateUtils.now()) + " - " + "统计agent数量";
        this.planCode = costCell.getPlanCode();
    }

    private String generateBizOrderNo(CostCell costCell) {
        long dayHeadSecond = DateUtils.getDayHeadSecond();
        return costCell.getTenantCode() + "-" + dayHeadSecond;
    }

}
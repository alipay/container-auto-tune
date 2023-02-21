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
package com.alipay.autotuneservice.model.notice;

import lombok.Getter;

/**
 * @author huoyuqi
 * @version NoticeBottonType.java, v 0.1 2022年09月26日 7:16 下午 huoyuqi
 */
@Getter
public enum NoticeButtonType {

    PLAN_DETAIL("查看计划详情"),
    CONFIRM("确认"),
    DETAIL("查看详情");

    private String type;

    NoticeButtonType(String type){
        this.type = type;
    }

}
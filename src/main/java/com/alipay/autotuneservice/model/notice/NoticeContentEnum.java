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
 * @version NoticeContentEnum.java, v 0.1 2022年09月26日 7:13 下午 huoyuqi
 */
@Getter
public enum NoticeContentEnum {

    SUBMIT("您的调优计划已开启", "开启了一个新的调优计划测试。"),
    EXPECT("您的调优计划已进入预期评估阶段", "进入了预期评估阶段测试。"),
    PARAM("您的调优计划已进入参数展示阶段", "进入了参数展示阶段测试。"),
    BATCH("您的调优计划已进入分批处理阶段", "进入了分批处理阶段测试。"),
    FINISH("您的调优计划已完成", "已完成，可查看调优结果测试。"),
    CONFIRM("您的调优计划待确认", "进入了调参进程阶段，需要人工确认下发参数测试"),
    CANCEL("您的调优计划已取消", "已取消，可查看调优流程测试。"),

    ALARM("报警详情","");

    private final String title;
    private final String content;

    NoticeContentEnum(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
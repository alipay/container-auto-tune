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
package com.alipay.autotuneservice.controller.model;

import lombok.Data;

import java.util.List;

/**
 * @author huoyuqi
 * @version GlobalAppVO.java, v 0.1 2022年04月27日 3:07 下午 huoyuqi
 */
@Data
public class GlobalAppListVO {

    /**
     * 检查应用数量
     */
    private Integer     checkedAppNum;

    /**
     * 未检查应用数量
     */
    private Integer     uncheckAppNum;

    /**
     * 应用列表
     */
    private List<AppVO> appList;
}
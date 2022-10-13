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
package com.alipay.autotuneservice.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 检查水平权限，用户只能使用自己的资源
 *
 * @author dutianze
 * @version ResourcePermission.java, v 0.1 2022年05月27日 14:11 dutianze
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ResourcePermission {

    /**
     * 入参的名字
     *
     * eg.
     *
     * func(Integer id)
     * path:id
     *
     * eg.
     *
     * class Model {
     * Integer id;
     * }
     * func(Model model)
     *
     * path:model.id
     */
    String path();

    /**
     * 查询资源使用哪个表
     */
    ResourceType type();

    enum ResourceType {

        /**
         * app_info
         */
        APP_ID,

        /**
         * tune_pipeline
         */
        PIPELINE_ID,

        /**
         * tune_plan
         */
        PLAN_ID
    }
}
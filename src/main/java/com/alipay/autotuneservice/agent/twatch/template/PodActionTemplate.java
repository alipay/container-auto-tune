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
package com.alipay.autotuneservice.agent.twatch.template;

import com.alipay.autotuneservice.agent.twatch.ActionTemplate;
import com.alipay.autotuneservice.model.agent.AutoTuneField;

import java.util.List;

/**
 * @author chenqu
 * @version : PodActionTemplate.java, v 0.1 2022年04月13日 16:53 chenqu Exp $
 */
public abstract class PodActionTemplate extends ActionTemplate {

    public final AutoTuneField<String>       PODNAME     = createField(POD_NAME);
    public final AutoTuneField<String>       CONTAINERID = createField(CONTAINER_ID);
    public final AutoTuneField<List<String>> CMDNAME     = createField(CMD_NAME);

}
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
package com.alipay.autotuneservice.model.tune;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * @author chenqu
 * @version : MetaDataType.java, v 0.1 2022年03月29日 14:30 chenqu Exp $
 */
public enum TuneTaskStatus {

    INIT, RUNNING, NEXT, OPTIMIZE, CANCEL, FINISH;

    private static final List<TuneTaskStatus> FINAL_LIST = ImmutableList.of(FINISH, NEXT, CANCEL,
                                                             OPTIMIZE);

    public boolean isFinal() {
        return FINAL_LIST.contains(this);
    }
}
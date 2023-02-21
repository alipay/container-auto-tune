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
package com.alipay.autotuneservice.agent.twatch.model;

import com.google.common.base.Preconditions;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

@Data
public class ActionMethodRequest implements Serializable {
    /**
     * 用于反射的方法名, 与methodBody里的方法名要对应
     **/
    private String       methodName;
    /**
     * 用法反射方法的参数
     **/
    private Object[]     methodArgs;
    /**
     * 用法反射的类型
     **/
    private Class[]      classTypes;
    /**
     * 反射方法, 方法里的类非java自带的要写带包名的类
     **/
    private String       methodBody;
    /**
     * 要导入的包
     **/
    private List<String> importPkg;
    /**
     * 是否刷新
     **/
    private boolean      refresh = Boolean.FALSE;

    public void checkArgument() {
        Preconditions.checkArgument(StringUtils.isNotBlank(methodName), "methodName不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(methodBody), "methodBody不能为空");
    }

    @Override
    public String toString() {
        return "ActionMethodRequest(methodArgs="
               + java.util.Arrays.deepToString(this.getMethodArgs());
    }
}

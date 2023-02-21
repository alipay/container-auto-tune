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
package com.alipay.autotuneservice.base.mail.model;


/**
 * @author zhaoxing
 * @version BodyTypeEnum.java, v 0.1 2022年02月22日 3:02 下午 zhaoxing
 */
public enum BodyTypeEnum {

    TEXT("text/plain; charset=UTF-8"),

    HTML("text/html; charset=UTF-8"),

    ;

    private String code;

    BodyTypeEnum(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
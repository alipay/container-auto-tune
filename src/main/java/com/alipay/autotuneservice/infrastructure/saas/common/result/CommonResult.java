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
package com.alipay.autotuneservice.infrastructure.saas.common.result;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 *
 * @author J
 * @version $Id: CommonResult.java, v 0.1 2022年03月03日 4:44 PM J Exp $
 */
public class CommonResult<T> implements Serializable {

    private static final long serialVersionUID = 1803349804344491398L;

    private Boolean           isSuccess;

    /** 结果码*/
    private String            resultCode;

    /** 结果描述 */
    private String            resultMsg;

    private T                 resultSet;

    public CommonResult() {
        super();
    }

    public CommonResult(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    /**
     * Getter method for property <tt>isSuccess</tt>.
     *
     * @return property value of isSuccess
     */
    public Boolean getSuccess() {
        return isSuccess;
    }

    /**
     * Setter method for property <tt>isSuccess</tt>.
     *
     * @param success  value to be assigned to property isSuccess
     */
    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    /**
     * Getter method for property <tt>resultCode</tt>.
     *
     * @return property value of resultCode
     */
    public String getResultCode() {
        return resultCode;
    }

    /**
     * Setter method for property <tt>resultCode</tt>.
     *
     * @param resultCode  value to be assigned to property resultCode
     */
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * Getter method for property <tt>resultMsg</tt>.
     *
     * @return property value of resultMsg
     */
    public String getResultMsg() {
        return resultMsg;
    }

    /**
     * Setter method for property <tt>resultMsg</tt>.
     *
     * @param resultMsg  value to be assigned to property resultMsg
     */
    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    /**
     * Getter method for property <tt>resultSet</tt>.
     *
     * @return property value of resultSet
     */
    public T getResultSet() {
        return resultSet;
    }

    /**
     * Setter method for property <tt>resultSet</tt>.
     *
     * @param resultSet  value to be assigned to property resultSet
     */
    public void setResultSet(T resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
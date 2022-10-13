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
package com.alipay.autotuneservice.model.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.google.common.base.Preconditions;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author dutianze
 * @version UserInfoRepository.java, v 0.1 2022年03月07日 16:10 dutianze
 */
@Data
@NoArgsConstructor
public class UserInfo implements Serializable {

    private Integer       id;
    private String        accountId;
    private AccessToken   accessToken;
    private String        userCompany;
    private String        userName;
    private String        tenantCode;
    private String        productAccountId;
    private String        planCode;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdTime;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updatedTime;

    public UserInfo(String accountId, String tenantCode, String productAccountId, String planCode) {
        Preconditions.checkNotNull(accountId);
        Preconditions.checkNotNull(tenantCode);
        Preconditions.checkNotNull(productAccountId);

        this.accountId = accountId;
        this.tenantCode = tenantCode;
        this.productAccountId = productAccountId;
        this.planCode = planCode;
    }

    public void generateAccessToken() {
        this.accessToken = AccessToken.generateAccessToken();
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = new AccessToken(accessToken);
    }

    public String getAccessToken() {
        return accessToken.getValue();
    }

    public boolean isValid() {
        return accessToken != null && StringUtils.isNotEmpty(accessToken.getValue());
    }

    public boolean tenantCodeIsValid(@Nonnull String tenantCode) {
        return tenantCode.equals(this.tenantCode);
    }
}
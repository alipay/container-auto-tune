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

import com.alipay.autotuneservice.controller.StorageController;
import com.alipay.autotuneservice.util.HttpUtil;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dutianze
 * @version PodAttach.java, v 0.1 2022年06月17日 11:28 dutianze
 */
@Data
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class PodAttach {

    /**
     * @see StorageController#downloadAttachAgentFile
     */
    private static final String ATTACH_PATH = "/api/storage/installTuneAgent.sh";

    private Integer             id;
    private String              accessToken;
    private Integer             podId;
    private PodAttachStatus     status      = PodAttachStatus.NOT_INSTALLED;
    private LocalDateTime       createdTime;
    private LocalDateTime       updatedTime;

    public boolean cantInstall() {
        return PodAttachStatus.INSTALLED.equals(status)
               || PodAttachStatus.INSTALLING.equals(status);
    }

    public String attachDownloadCmd() {
        Preconditions.checkNotNull(accessToken);
        Preconditions.checkNotNull(id);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("accessToken", this.getAccessToken());
        parameters.put("attachId", String.valueOf(this.getId()));
        String shellUrl = HttpUtil.buildInnerUrl(ATTACH_PATH, parameters);
        return String.format("wget -O /tmp/run.sh %s", shellUrl);
    }

    public String attachInstallCmd(Integer processId) {
        return String.format("sh /tmp/run.sh %s", processId);
    }
}
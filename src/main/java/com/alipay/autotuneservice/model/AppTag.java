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
package com.alipay.autotuneservice.model;

import com.alipay.autotuneservice.dao.jooq.tables.records.AppInfoRecord;
import com.alipay.autotuneservice.service.algorithmlab.GarbageCollector;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author dutianze
 * @version AppTag.java, v 0.1 2022年05月16日 16:56 dutianze
 */
@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
public class AppTag implements Serializable {

    private Lang             lang;
    private String           javaVersion;
    private GarbageCollector collector;
    private boolean          installAgent;
    private boolean          installDockFile;
    private Long             lastModifyTime;

    public static AppTag ofLang(Lang lang) {
        return AppTag.builder()
                .withLang(lang)
                .build();
    }

    public static AppTag ofLastModifyTime(Long lastModifyTime){
        return AppTag.builder()
                .withLastModifyTime(lastModifyTime)
                .build();
    }


    public static AppTag of(Boolean installAgent, Boolean installDockFile) {
        return AppTag.builder()
                .withInstallAgent(installAgent)
                .withInstallDockFile(installDockFile)
                .build();
    }

    public void resetJvmCollector(AppInfoRecord appInfoRecord) {
        String appDefaultJvm = appInfoRecord.getAppDefaultJvm();
        if (StringUtils.isEmpty(appDefaultJvm)) {
            return;
        }
        this.collector = GarbageCollector.matchGarbageCollectorByJvmOpt(appDefaultJvm);
    }

    public enum Lang {
        JAVA
    }

    public static boolean equalIfNotNull(Object attr1, Object attr2) {
        return attr1 == null || attr2 == null || attr1.equals(attr2);
    }

    /**
     * if thatAppTag is null, will return true
     *
     * @param thatAppTag match condition
     * @return if match or not
     */
    public boolean matchAppTag(AppTag thatAppTag) {
        if (thatAppTag == null) {
            return true;
        }
        return AppTag.equalIfNotNull(this.lang, thatAppTag.getLang()) &&
                AppTag.equalIfNotNull(this.javaVersion, thatAppTag.getJavaVersion()) &&
                AppTag.equalIfNotNull(this.collector, thatAppTag.getCollector());
    }
}
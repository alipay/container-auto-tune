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

import com.alibaba.fastjson.JSONObject;
import com.alipay.autotuneservice.dao.jooq.tables.records.JavaInfoRecord;
import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author chenqu
 * @version : JavaInfo.java, v 0.1 2022年01月26日 16:21 chenqu Exp $
 */
@Data
public class JavaInfo {

    /**
     * 应用名
     */
    private String appName;
    /**
     * namespace
     */
    private String nameSpace;
    /**
     * jvm版本
     */
    private String version;

    /**
     * jvm安装目录
     */
    private String jvmHome;

    /**
     * classpath
     */
    private String classPath;

    /**
     * libraryPath
     */
    private String libraryPath;

    /**
     * 当前用户的名称
     */
    private String userName;

    /**
     * 用户当前的工作目录
     */
    private String userDir;

    /**
     * 执行时间
     */
    private String excTime;

    /**
     * jar包容器
     */
    private Set<String> jarLibs = new HashSet<>();

    /**
     * os version
     */
    private String osVersion;

    /**
     * os arch
     */
    private String osArch;

    /**
     * 启动参数
     */
    private List<String> inputArguments;

    private Map<String, String> jarPathMap = new HashMap<String, String>();

    private Integer pid;

    /**
     * 宿主机Ip
     *
     * @return
     */
    private String hostName;

    @Override
    public String toString() {
        return "JavaInfo{" +
                "nameSpace='" + nameSpace + '\'' +
                ", appName='" + appName + '\'' +
                ", version='" + version + '\'' +
                ", jvmHome='" + jvmHome + '\'' +
                ", classPath='" + classPath + '\'' +
                ", libraryPath='" + libraryPath + '\'' +
                ", userName='" + userName + '\'' +
                ", userDir='" + userDir + '\'' +
                ", excTime='" + excTime + '\'' +
                ", jarLibs=" + jarLibs +
                ", osVersion='" + osVersion + '\'' +
                ", osArch='" + osArch + '\'' +
                ", inputArguments=" + inputArguments +
                ", hostName='" + hostName + '\'' +
                ", jarPathMap=" + jarPathMap +
                '}';
    }

    public JavaInfoRecord toRecord() {
        JavaInfoRecord record = new JavaInfoRecord();
        record.setAppName(this.appName);
        record.setNamespace(this.nameSpace);
        record.setJvmHome(this.jvmHome);
        record.setVersion(this.version);
        record.setClassPath(this.classPath);
        record.setLibraryPath(this.libraryPath);
        record.setUserName(this.userName);
        record.setUserDir(this.userDir);
        record.setExecTime(this.excTime);
        record.setJavaLibs(JSONObject.toJSONString(this.jarLibs));
        record.setOsVersion(this.osVersion);
        record.setOsArch(this.osArch);
        record.setInputArguments(JSONObject.toJSONString(this.inputArguments));
        record.setHostName(this.hostName);
        record.setPid(this.pid);
        return record;
    }
}

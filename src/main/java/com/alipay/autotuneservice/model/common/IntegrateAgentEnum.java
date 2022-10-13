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

import org.apache.commons.lang3.StringUtils;

/**
 * @author huangkaifei
 * @version : IntegrateAgentEnum.java, v 0.1 2022年07月04日 9:50 PM huangkaifei Exp $
 */
public enum IntegrateAgentEnum {
    /**
     * docker方式集成agent安装
     */
    WITH_DOCKER("docker", "config_java_opts.flt", "config_java_opts.sh", "使用dockerfile集成agent"),
    /**
     * Yaml方式集成agent安装
     */
    WITH_YAML("yaml", "config_java_opts_with_yaml.flt", "config_java_opts.sh", "使用yaml集成agent"), ;

    private String type;
    private String templateFile;
    private String shellScript;
    private String desc;

    IntegrateAgentEnum(String type, String templateFile, String shellScript, String desc) {
        this.type = type;
        this.templateFile = templateFile;
        this.shellScript = shellScript;
        this.desc = desc;
    }

    public static IntegrateAgentEnum find(String type) {
        if (StringUtils.isEmpty(type)) {
            return WITH_YAML;
        }
        for (IntegrateAgentEnum agentEnum : values()) {
            if (StringUtils.equals(agentEnum.getType(), type)) {
                return agentEnum;
            }
        }
        throw new UnsupportedOperationException(String.format("type=%s is not supported!", type));
    }

    public String getType() {
        return type;
    }

    public String getTemplateFile() {
        return templateFile;
    }

    public String getShellScript() {
        return shellScript;
    }

    public String getDesc() {
        return desc;
    }
}
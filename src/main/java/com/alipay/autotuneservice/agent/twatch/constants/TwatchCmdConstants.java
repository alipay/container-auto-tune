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
package com.alipay.autotuneservice.agent.twatch.constants;

/**
 * @author huangkaifei
 * @version : TwatchCmdConstants.java, v 0.1 2022年05月09日 4:53 PM huangkaifei Exp $
 */
public class TwatchCmdConstants {

    public static final String AGENT_HEALTH_CHECK_FILE           = "/tmp/AGENT_HEALTH_CHECK";
    public static final String CHECK_AUTO_TUNE_AGENT_EXIST_REG   = "^/tmp/AUTO_TUNE_AGENT$";
    public static final String CHECK_AUTO_TUNE_AGENT_INSTALL_CMD = "ls /tmp/AUTO_TUNE_AGENT";
    public static final String CAT_AGENT_HEALTH_CHECK_FILE_CMD   = String.format("cat %s",
                                                                     AGENT_HEALTH_CHECK_FILE);
    /**
     * 查看应用final的jvm启动参数
     */
    public static final String CAT_FINAL_JVM_OPTS_CMD            = "cat /tmp/FINAL_START_JVM_OPTS_FILE";

}
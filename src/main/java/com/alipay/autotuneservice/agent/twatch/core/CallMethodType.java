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
package com.alipay.autotuneservice.agent.twatch.core;

import com.alipay.autotuneservice.agent.twatch.template.PodActionTemplate;
import com.alipay.autotuneservice.agent.twatch.template.ProcessActionTemplate;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author chenqu
 * @version : CallMethodType.java, v 0.1 2022年04月13日 16:46 chenqu Exp $
 */
public class CallMethodType {

    public static class POD {

        public static final PodActionTemplate EXEC_CMD   = new PodActionTemplate() {
                                                             @Override
                                                             public String methodBody() {
                                                                 return "public String execCmd(String containerId, String cmd) {\n"
                                                                        + "            // creat exec instance\n"
                                                                        + "            com.github.dockerjava.api.command.ExecCreateCmd execCreateCmd = dockerProxy.getDockerClient()"
                                                                        + ".execCreateCmd(containerId);\n"
                                                                        + "            ExecCreateCmdResponse execInstance = (ExecCreateCmdResponse) execCreateCmd\n"
                                                                        + "                    .withAttachStdout(Boolean.TRUE)\n"
                                                                        + "                    .withAttachStderr(Boolean.TRUE)\n"
                                                                        + "                    .withCmd(cmd.split(\" \"))\n"
                                                                        + "                    .exec();\n"
                                                                        + "            // start exec cmd\n"
                                                                        + "            ExecCmdResultCallBack cmdCallback = new ExecCmdResultCallBack();\n"
                                                                        + "            try {\n"
                                                                        + "                dockerProxy.getDockerClient().execStartCmd(execInstance.getId()).exec(cmdCallback);\n"
                                                                        + "                cmdCallback.awaitCompletion();\n"
                                                                        + "                return cmdCallback.getOutput();\n"
                                                                        + "            } catch (Exception e) {\n"
                                                                        + "                //do noting\n"
                                                                        + "                e.printStackTrace();\n"
                                                                        + "                return \"\";\n"
                                                                        + "            }\n"
                                                                        + "        }";
                                                             }

                                                             @Override
                                                             public List<String> importPkg() {
                                                                 return Lists
                                                                     .newArrayList(
                                                                         "com.github.dockerjava.core.command.ExecStartResultCallback",
                                                                         "com.alipay.twatch.proxy.ExecCmdResultCallBack",
                                                                         "java.nio.charset.StandardCharsets",
                                                                         "java.io.ByteArrayOutputStream",
                                                                         "com.alipay.twatch.proxy.DockerProxy",
                                                                         "com.alipay.twatch.proxy.ExecCmdResultCallBack",
                                                                         "com.github.dockerjava.api.command.ExecCreateCmdResponse",
                                                                         "org.apache.commons.lang3.StringUtils");
                                                             }

                                                             @Override
                                                             public String methodName() {
                                                                 return "execCmd";
                                                             }

                                                             @Override
                                                             public Class[] classTypes() {
                                                                 return new Class[] { String.class,
                                                                         String.class };
                                                             }
                                                         };

        public static final PodActionTemplate GET_ENV    = new PodActionTemplate() {

                                                             @Override
                                                             public String methodBody() {
                                                                 return "public String getEnv(String containerId) {\n"
                                                                        + "    // creat exec instance\n"
                                                                        + "    com.github.dockerjava.api.command.ExecCreateCmd execCreateCmd = dockerProxy.getDockerClient().execCreateCmd"
                                                                        + "(containerId);\n"
                                                                        + "    ExecCreateCmdResponse execInstance = (ExecCreateCmdResponse) execCreateCmd\n"
                                                                        + "            .withAttachStdout(Boolean.TRUE)\n"
                                                                        + "            .withAttachStderr(Boolean.TRUE)\n"
                                                                        + "            .withCmd(new String[] {\"env\"})\n"
                                                                        + "            .exec();\n"
                                                                        + "    // start exec cmd\n"
                                                                        + "    ExecCmdResultCallBack cmdCallback = new ExecCmdResultCallBack();\n"
                                                                        + "    try {\n"
                                                                        + "        dockerProxy.getDockerClient().execStartCmd(execInstance.getId()).exec(cmdCallback);\n"
                                                                        + "        cmdCallback.awaitCompletion();\n"
                                                                        + "        return cmdCallback.getOutput();\n"
                                                                        + "    } catch (Exception e) {\n"
                                                                        + "        //do noting\n"
                                                                        + "        e.printStackTrace();\n"
                                                                        + "        return \"\";\n"
                                                                        + "    }\n" + "}";
                                                             }

                                                             @Override
                                                             public List<String> importPkg() {
                                                                 return Lists
                                                                     .newArrayList(
                                                                         "com.github.dockerjava.core.command.ExecStartResultCallback",
                                                                         "com.alipay.twatch.proxy.ExecCmdResultCallBack",
                                                                         "java.nio.charset.StandardCharsets",
                                                                         "java.io.ByteArrayOutputStream",
                                                                         "com.alipay.twatch.proxy.DockerProxy",
                                                                         "com.alipay.twatch.proxy.ExecCmdResultCallBack",
                                                                         "com.github.dockerjava.api.command.ExecCreateCmdResponse",
                                                                         "org.apache.commons.lang3.StringUtils");
                                                             }

                                                             @Override
                                                             public String methodName() {
                                                                 return "getEnv";
                                                             }

                                                             @Override
                                                             public Class[] classTypes() {
                                                                 return new Class[] { String.class };
                                                             }
                                                         };

        public static final PodActionTemplate EXEC_STATS = new PodActionTemplate() {

                                                             @Override
                                                             public String methodBody() {
                                                                 return "public String execStats(String containerId) {\n"
                                                                        + "        try {\n"
                                                                        + "            CountDownLatch countDownLatch = new CountDownLatch(1);\n"
                                                                        + "            StatusCmdCallBack statusCmdCallBack = new StatusCmdCallBack(countDownLatch);\n"
                                                                        + "            dockerProxy.getDockerClient().statsCmd(containerId).withNoStream(true).exec(statusCmdCallBack);\n"
                                                                        + "            countDownLatch.await(3L, TimeUnit.SECONDS);\n"
                                                                        + "            return JSONObject.toJSONString(statusCmdCallBack.getStatistics());\n"
                                                                        + "        } catch (Exception e) {\n"
                                                                        + "            e.printStackTrace();\n"
                                                                        + "            return null;\n"
                                                                        + "        }\n" + "    }";
                                                             }

                                                             @Override
                                                             public List<String> importPkg() {
                                                                 return Lists
                                                                     .newArrayList(
                                                                         "com.github.dockerjava.core.command.ExecStartResultCallback",
                                                                         "com.alipay.twatch.proxy.ExecCmdResultCallBack",
                                                                         "java.nio.charset.StandardCharsets",
                                                                         "java.io.ByteArrayOutputStream",
                                                                         "com.alipay.twatch.proxy.DockerProxy",
                                                                         "com.alipay.twatch.proxy.ExecCmdResultCallBack",
                                                                         "com.github.dockerjava.api.command.ExecCreateCmdResponse",
                                                                         "org.apache.commons.lang3.StringUtils",
                                                                         "java.util.concurrent.CountDownLatch");
                                                             }

                                                             @Override
                                                             public String methodName() {
                                                                 return "execStats";
                                                             }

                                                             @Override
                                                             public Class[] classTypes() {
                                                                 return new Class[] { String.class };
                                                             }
                                                         };
    }

    public static class PROCESS {

        public static final ProcessActionTemplate LIST = new ProcessActionTemplate() {

                                                           @Override
                                                           public String methodBody() {
                                                               return "public TopContainerResponse asyncListProcess(String containerId){\n       TopContainerResponse aux = dockerProxy"
                                                                      + ".getDockClient().topContainerCmd(containerId).withPsArgs(\"aux\").exec();\n        return aux;\n    }";
                                                           }

                                                           @Override
                                                           public List<String> importPkg() {
                                                               return Lists
                                                                   .newArrayList("com.github.dockerjava.api.command.TopContainerResponse");
                                                           }

                                                           @Override
                                                           public String methodName() {
                                                               return "asyncListProcess";
                                                           }

                                                           @Override
                                                           public Class[] classTypes() {
                                                               return new Class[] { String.class };
                                                           }
                                                       };
    }
}
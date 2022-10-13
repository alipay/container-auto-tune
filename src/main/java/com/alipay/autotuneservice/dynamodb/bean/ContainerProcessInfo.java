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
package com.alipay.autotuneservice.dynamodb.bean;

import lombok.Data;

/**
 * @author huangkaifei
 *
 * 进程的信息: "USER","PID","%CPU","%MEM","VSZ","RSS","TTY","STAT","START","TIME","COMMAND"
 *
 * @version : ContainerProcessInfo.java, v 0.1 2022年04月18日 2:22 PM huangkaifei Exp $
 */
@Data
public class ContainerProcessInfo {
    private String  podName;
    private String  containerId;
    private long    gmtCreated;

    private String  USER;
    private long    PID;
    private double  CPU;
    private double  MEM;
    // Virtual Memory Size （虚拟内存大小）的缩写。 它包含了进程所能访问的所有内存，包含了被换出的内存，被分配但是还没有被使用的内存，以及动态库中的内存
    private long    VSZ;
    // RSS是Resident Set Size（常驻内存大小）的缩写，用于表示进程使用了多少内存（RAM中的物理内存），RSS不包含已经被换出的内存。RSS包含了它所链接的动态库并且被加载到物理内存中的内存。RSS还包含栈内存和堆内存
    private long    RSS;
    private String  TTY;
    // 进程状态
    private String  STAT;
    // 进程启动时间
    private String  START;
    private String  TIME;
    // 进程启动执行的命令
    private String  COMMAND;
    private Integer appId;

    public String getContainerId() {
        return containerId;
    }

    public long getGmtCreated() {
        return gmtCreated;
    }
}